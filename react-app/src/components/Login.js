import React, { Component } from "react";
import axios from "axios";

class Login extends Component {
  state = {
    username: "",
    password: "",
    loading: false,
    error: null,
    user: null,
  };

  handleInputChange = (e) => {
    const { name, value } = e.target;
    this.setState({ [name]: value });
  };

  handleSubmit = (e) => {
    e.preventDefault();

    const { username, password } = this.state;
    this.setState({ loading: true, error: null });

    axios.post("http://localhost:8080/api/v1/user/login", { username, password })
      .then((response) => {
        const { accessToken, refreshToken } = response.data;

        localStorage.setItem("accessToken", accessToken);
        localStorage.setItem("refreshToken", refreshToken);

        console.log("AccessToken:", accessToken); // 로그에 AccessToken 출력

        this.setState({
          loading: false,
          user: response.data, // assuming the server sends user data on successful login
        });
      })
      .catch((error) => {
        this.setState({
          loading: false,
          error: "로그인에 실패하였습니다. 사용자명과 비밀번호를 확인해주세요.",
        });
      });
  };

  render() {
    const { username, password, loading, error, user } = this.state;

    if (user) {
      return (
        <div>
          <h2>로그인 성공</h2>
          <p>환영합니다, {user.username}!</p>
        </div>
      );
    } else {
      return (
        <div>
          <h2>Login</h2>
          {error && <p style={{ color: 'red' }}>{error}</p>}
          <form onSubmit={this.handleSubmit}>
            <div>
              <input
                type="text"
                name="username"
                value={username}
                onChange={this.handleInputChange}
                placeholder="Username"
              />
            </div>
            <div>
              <input
                type="password"
                name="password"
                value={password}
                onChange={this.handleInputChange}
                placeholder="Password"
              />
            </div>
            <button type="submit" disabled={loading}>
              {loading ? '로그인 중...' : 'Login'}
            </button>
          </form>
        </div>
      );
    }
  }
}

export default Login;