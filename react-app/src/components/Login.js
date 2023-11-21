import React, { Component } from "react";
import axios from "axios";

class Login extends Component {
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

    axios.post("http://localhost:8080/api/v1/user/login", { username, password })
    .then((response) => {
      alert("로그인에 성공하였습니다.");
      console.log(response.status);
      this.setState(() => { loggedIn: true });
    }).catch((error) => { 
      alert(error);
    });
  }

  render() {
    const { username, password, loggedIn } = this.state;

    if (loggedIn) {
      return (
        <div>
          <h2>로그인 성공</h2>
          <p>환영합니다, {username}!</p>
        </div>
      );
    } else {
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
}

export default Login;