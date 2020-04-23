const User = require('../models/User');
const Turn = require('../models/Turn');

let users = [];

module.exports = (io) => {
    io.on('connection', (socket) => {
        socket.on('init', async () => {
            const supportUsers = await User.find({ type: { $ne: 'A' } });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('init', (type, pref) => {
            getTruns(io, type, pref)
        });

        socket.on('login', async (username, pass) => {
            const user = await User.findOne({ usuario: username });
            if (!user) {
                socket.emit('logged', false, "", "El colaborador ingresado no existe.")
            } else {
                if (pass != user.pass) {
                    socket.emit('logged', false, user.type, "Contraseña incorrecta.")
                } else {
                    socket.emit('logged', true, user.type, "Acceso Concedido.", user.usuario)
                    socket.nickname = { type: user.type, name: user.usuario };
                    if (user.type == 'P') io.sockets.emit('preferencesConnected', true)
                    users.push(socket)
                }
            }

        });

        socket.on('createUser', async user => {
            await new User(JSON.parse(user)).save();
            const supportUsers = await User.find({ type: { $ne: 'A' } });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('modificarUsuario', async user => {
            const userObj = JSON.parse(user);
            await User.findByIdAndUpdate(userObj._id, userObj);
            const supportUsers = await User.find({ type: { $ne: 'A' } });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('eliminarUsuario', async id => {
            await User.findByIdAndDelete(id);
            const supportUsers = await User.find({ type: { $ne: 'A' } });
            io.sockets.emit('users', supportUsers)
        });

        socket.on('newClient', async (client) => {
            setTimeout(() => socket.emit('alert', 'Tiempo Expirado.'), 1 * 60000);
        });

        socket.on('createTransaction', async (type, pref) => {
            const correl = await Turn.find({ type }).countDocuments() + 1;
            const turn = new Turn({ type, correl, pref, state: "E" }); //E = Esperando, A: Atendido, X: Cancelado
            await turn.save();
            socket.emit('newTransaction', `${type}${addZero(correl)}`);
            console.log(`${type}${addZero(correl)}`);
            getTruns(io, type, pref)
        });

        socket.on('logout', (type, name) => {
            users = users.filter(socket => socket.nickname.type != type && socket.nickname.name != name)
            if (type == 'P') io.sockets.emit('preferencesConnected', false)
        });

        socket.on('atender', async req => { // {_id, type: "C", correl: 1, pref: true}
            const turn = JSON.parse(req)
            await Turn.findByIdAndUpdate(turn._id, new Turn(turn))
            getTruns(io, turn.type, turn.pref)
        })

        socket.on('llamar', (type, username, codigo) => { // {_id, type: "C", correl: 1, pref: true}
            const name = getTableName(type, username)
            console.log("Ticket Numero", codigo, name)
            io.sockets.emit("llamarTicket", codigo, name)
        })
    });
};

const getTruns = async (io, type, pref) => {
    const query = { type, pref, state: "E" };
    const turns = await Turn.find(query);
    const t = pref ? "P" : type;
    getAllTruns(io)
    io.sockets.emit(`newTurn${t}`, turns)
};

const getAllTruns = async (io) => {
    const query = { state: "E" };
    const turns = await Turn.find(query);

    io.sockets.emit(`newTurn`, turns)
};

function addZero(number) {
    if (number < 10) return `00${number}`;
    if (number < 100) return `0${number}`;
    return number;
}

function filterByType(type) {
    return users.filter(user => user.nickname.type == type)
}

function filterByUsername(usuario) {
    return users.filter(user => user.nickname.name == usuario)
}

function getTableName(type, usuario) {
    const usersByTypes = filterByType(type)
    const usersByUsuario = filterByUsername(usuario)
    console.log(usersByTypes, usersByUsuario)
    const user = usersByUsuario.length > 0 ? usersByUsuario[0] : {}
    const number = usersByTypes.indexOf(user)
    const name = type == "C" ? "Ventanilla" : type == "R" ? "Escritorio" : "Mesa"
    return `${name} ${number + 1}`
}