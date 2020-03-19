let users = {}

module.exports = (io) => {
    io.on('connection', (socket) => {
        socket.on('connected', data => {
            socket.nickname = data;
            socket.emit('getUsername', data);
            users[data] = socket;
            console.log("User Connected " + data)
        })
        
        socket.on('loginAdmin', (user, pass) => {
            if(user == 'Admin' && pass == '1234'){
                socket.emit('logged', true) 
            }else{
                socket.emit('logged', false)
            }
        });

        socket.on('loginSupport', (user, pass) => {
            if(user == 'Support' && pass == '1234'){
                socket.emit('logged', true) 
            }else{
                socket.emit('logged', false)
            }
        });

        socket.on('newClient', async (client) => {
            setTimeout(() => socket.emit('alert','Tiempo Expirado.'), 1 * 60000);
        })
    });
}