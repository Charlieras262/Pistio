const User = require('../models/User')
let users = {}

module.exports = (io) => {
    io.on('connection', (socket) => {
        socket.on('connected', data => {
            socket.nickname = data;
            socket.emit('getUsername', data);
            users[data] = socket;
            console.log("User Connected " + data)
        })

        socket.on('login', async (username, pass, type) => {
            const user = await User.findOne({ usuario: username });
            if (!user) {
                socket.emit('logged', false)
            } else {
                if (pass == user.pass && type == user.type) {
                    socket.emit('logged', true, user.type)
                } else {
                    socket.emit('logged', false, user.type)
                }
            }
        });

        socket.on('createUser', async user => {
            await new User(JSON.parse(user)).save()
            const supportUsers = await User.find()
            socket.emit('users', supportUsers)
        });

        socket.on('newClient', async (client) => {
            setTimeout(() => socket.emit('alert', 'Tiempo Expirado.'), 1 * 60000);
        })
    });
}