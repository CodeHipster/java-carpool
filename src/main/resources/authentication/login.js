
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
            fetch("/auth/login",
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
            fetch("/auth/login",
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

Vue.component('register',{
    template:`<div>
    <input type="email" v-model="registration.email" name="email"/>
    <input type="password" v-model="registration.password" name="password">
    <input type="button" value="register" v-on:click="register">
</div>`,
    data:function(){
        return {registration:{email:'', password:''}}
    },
    methods:{
        register: function(){
            //TODO: verify input
            fetch("/auth/register",
                {
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    method: "POST",
                    body: JSON.stringify(this.registration)
                })
                .then(function(res){ console.log(res) })
                .catch(function(res){ console.log(res) })
        }
    }
})

Vue.component('change-password',{
    template:`<div>
    <input type="email" v-model="changeData.email" name="email"/>
    <input type="password" v-model="changeData.oldPassword" name="old-password">
    <input type="password" v-model="changeData.newPassword" name="new-password">
    <input type="button" value="change password" v-on:click="changePassword">
</div>`,
    data:function(){
        return {changeData:{email:'', oldPassword:'', newPassword:''}}
    },
    methods:{
        changePassword: function(){
            //TODO: verify input
            fetch("/auth/change-password",
                {
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    method: "POST",
                    body: JSON.stringify(this.changeData)
                })
                .then(function(res){ console.log(res) })
                .catch(function(res){ console.log(res) })
        }
    }
})
Vue.component('reset-password',{})
Vue.component('verify',{})


var loginApp = new Vue({
    el: '#app'
})