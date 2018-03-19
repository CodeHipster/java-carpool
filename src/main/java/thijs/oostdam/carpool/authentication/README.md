## authentication

# login handler
LoginHandler provides an endpoint which can be called with an id(email) and password.
this will return a signed-id in the header

# authentication filter
The signed-id header is to be set on every request that goes through the AuthenticationFilter.
The filter verifies that the signed-id is signed with the private key that comes with the public key

# key pair provider
the key pair provider is a class that will provide a generated keypair. 
You will have to make sure that the same instance is used for signing and verification
For multi instance deployment you might want to build your own provider that stores it in a database somewhere.

# signed-id
The signed-id header contains a token consisting of 2 parts separated by a '.' 
the first part is the id(email) in base64 encoded format.
the second part is the signature in base64 encoded format.

# safety
As the saying goes: when it comes to security don't roll your own.
The implementation is safe enough for a showcasing app, but don't use it for something that is dear to you :-) 
(find a proper JWT implementation)


all functionality
- register with email and password
    - which gives acces, but user should not be able to participate
    - sends an email to user with a key to verify his email
- verify key from email
    - enables user to participate
- reset password
    changes password to a random value which is send to the user
- change password
    will change the password to the provided value
- login 
    user can login with email and password