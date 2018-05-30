
Vue.component('login', {
    template: `<div>
    <input type="email" v-model="login.email" name="email"/>
    <input type="password" v-model="login.password" name="password">
    
    
    <input type="button" value="login" v-on:click="loginWithPassword">
    <input type="button" value="loginWithEmail" v-on:click="loginWithEmail">
</div>`,
    data: function () {
        return {
            login: {email: '', password: ''}
        }
    },
    methods: {
        loginWithPassword: function () {
            //TODO: verify input
            fetch("/login",
                {
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    method: "POST",
                    body: JSON.stringify(this.login)
                })
                .then(function(res){ console.log(res) })
                .catch(function(res){ console.log(res) })
        },
        loginWithEmail: function() {
            //TODO: verify input
            fetch("/login/email",
                {
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    method: "POST",
                    body: JSON.stringify(this.login)
                })
                .then(function(res){ console.log(res) })
                .catch(function(res){ console.log(res) })
        }
    }
})

var loginApp = new Vue({
    el: '#app'
})