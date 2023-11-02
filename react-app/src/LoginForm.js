import React, { Component } from "react";

class loginForm extends Component {
  state = {
    username: "",
    password: "",
    loggedIn: false,
  };

  handleInputChange = (e) => {
    const { name, value } = e.target;
    this.setState({ [name]: value });
  };

  handleSubmit = (e) => {
    const { username, password } = this.state;

    if (username === "aaa" && password === "111") {
      this.setState({ loggedIn: true });
      alert("로그인 성공!");
    } else {
      alert("로그인 실패. 사용자 이름 또는 비밀번호가 잘못되었습니다.");
    }
  };

  render() {
    const { username, password, loggedIn } = this.state;

    if (loggedIn) {
      return (
        <div>
          <h2>로그인 성공</h2>
          <p>환영합니다, {username}!</p>
        </div>
      );
    }

    return (
      <div>
        <h2>Login</h2>
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
          <button type="submit">Login</button>
        </form>
      </div>
    );
  }
}

export default loginForm;