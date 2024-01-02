function loginCheck() {
    const token = getCookie('access_token');
    const url = "/api/login/login-check";
    if (token != null) {
        return fetch(url, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + token,
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json(); // JSON 데이터를 파싱하여 반환
            } else {
                throw new Error('Network response was not ok.');
            }
        })
        .then(data => {
            if (data === true || data === 'true') {
                return true;
            } else {
                return false;
            }
        })
        .catch(error => {
            console.error('오류 발생:', error);
            return false;
        });
    } else {
        return Promise.resolve(false); // 토큰이 없는 경우 에러 처리
    }
}

function getLoginInfo() {
    const url = "/api/login/login-info";
    const token = getCookie('access_token');

    if (token != null) {
        return fetch(url, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + token,
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json(); // JSON 데이터를 파싱하여 반환
            } else {
                throw new Error('Network response was not ok.');
            }
        })
        .then(data => {
            const map = {
                "firstName" : data.firstName,
                "lastName" : data.lastName,
            };
            return map;
        })
        .catch(error => {
            console.error('오류 발생:', error);
            throw error; // 에러를 다시 던져서 처리할 수 있도록 함
        });
    } else {
        return Promise.reject(new Error('Token is null')); // 토큰이 없는 경우 에러 처리
    }
}

function logout(){
    const url = '/api/login/logout-proc';
    const token = getCookie("access_token");

    fetch(url, {
          method: 'POST',
          headers: {
            'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token,
          },
          body: JSON.stringify({
              refreshToken: getCookie("refresh_token")
          })
        })
        .then(response => {
          if (response.ok) {
            deleteCookie('access_token');
            deleteCookie('refresh_token');
            location.reload(true);
          } else {
            console.log('실패');
          }
        })
        .catch(error => {
          console.log('오류 발생:', error);
        });
}

function setCookie(cookieName, value){
    var now = new Date();
    var expireTime = new Date(now.getTime() + 60 * 60 * 1000); // 현재 시간에 1시간을 더합니다.
    document.cookie = cookieName + '=' + value + ';expires=' + expireTime.toUTCString() + ';path=/';
}

function getCookie(cookieName) {
  const cookies = document.cookie.split('; '); // 쿠키 문자열을 ; 공백을 기준으로 분리하여 배열로 만듦
  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i].split('='); // 각 쿠키를 = 기준으로 분리하여 배열로 만듦
    const name = cookie[0];
    const value = cookie[1];
    if (name === cookieName) {
      return decodeURIComponent(value); // URI 디코딩하여 반환
    }
  }
  return null; // 해당하는 쿠키가 없을 경우 null 반환
}

function deleteCookie(cookieName){
    var now = new Date();
    var expireTime = new Date(now.getTime() - (365 * 24 * 60 * 60 * 1000)); //1년 전
    document.cookie = cookieName + '=;expires=' + expireTime.toUTCString() + ';path=/';
}

function goPage(url){
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + getCookie("access_token"),
      }
    })
    .then(response => {
      if (response.ok) {
        console.log('성공');
      } else {
        console.log('실패');
      }
    })
    .catch(error => {
      console.error('오류 발생:', error);
    });
}

function httpRequest(method, url, body, success, fail){
    fetch(url, {
        method: method,
        headers: {
            Authorization: "Bearer " + getCookie("access_token"),
            "Content-Type": "application/json"
        },
        body: body
    }).then((response) => {
        if (response.status === 200 || response.status == 201){
            return success()
        }
        const refresh_token = getCookie("refresh_token")
        // access_token 이 만료되어 권한이 없고, 리프레시 토큰이 있다면 그 리프레시 토큰을 이용해서 새로운 access token 을 요청
        if (response.status === 401 && refresh_token) {
            fetch("/api/login/reissue", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + getCookie("access_token"),
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    refresh_token: getCookie("refresh_token")
                })
            }).then((res) => {
                if (res.ok){
                    return res.json()
                }
            }).then((result) => {
                // refresh token 재발급에 성공하면 로컬 스토리지 값을 새로운 access token 으로 교체
                localStorage.setItem("access_token", result.accessToken)
                // 새로운 access token 으로 http 요청을 보낸다.
                httpRequest(method, url, body, success, fail)
            }).catch(error => {
                console.error('오류 발생:', error);
            });
        }else {
            return fail();
        }
    }).catch(error => {
        console.error('오류 발생:', error);
    });
}