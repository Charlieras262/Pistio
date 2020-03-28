const User = require('../models/User');
const five = require("johnny-five");
var board = null;

board = new five.Board();

module.exports = (io) => {
    board.on("ready", function () {
        
    });

    io.on('connection', (socket) => {
        socket.on('init', async () => {
            const supportUsers = await User.find({ type: 'S' });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('connected', data => {
            socket.nickname = data;
            socket.emit('getUsername', data);
        });

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
            await new User(JSON.parse(user)).save();
            const supportUsers = await User.find({ type: 'S' });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('modificarUsuario', async user => {
            const userObj = JSON.parse(user);
            await User.findByIdAndUpdate(userObj._id, userObj);
            const supportUsers = await User.find({ type: 'S' });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('eliminarUsuario', async id => {
            await User.findByIdAndDelete(id);
            const supportUsers = await User.find({ type: 'S' });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('newClient', async (client) => {
            setTimeout(() => socket.emit('alert', 'Tiempo Expirado.'), 1 * 60000);
        });

        socket.on('newTicket', state => {
            led = new five.Led(12);
            if(state){
                led.on();
            } else {
                led.off();
            }
        })
    });

};