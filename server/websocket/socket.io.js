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
            const user = users.find(user => user.nickname.pref == true)
            if(user != undefined && pref == false){
                socket.emit('preferencesConnected', true, user.nickname.type)
            }
        });

        socket.on('login', async (username, pass) => {
            const user = await User.findOne({ usuario: username });
            if (!user) {
                socket.emit('logged', false, "", "El colaborador ingresado no existe.")
            } else {
                if (pass != user.pass) {
                    socket.emit('logged', false, user.type, "Contraseña incorrecta.")
                } else {
                    socket.emit('logged', true, user.type, "Acceso Concedido.", user.usuario, user.pref)
                    socket.nickname = { type: user.type, name: user.usuario, pref: user.pref};
                    if(user.pref) io.sockets.emit('preferencesConnected', true, user.type)
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
            console.log(userObj)
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
            setTimeout(() => socket.emit('alert', 'Te quedan 5 minutos.'), 10 * 60000);
            setTimeout(() => socket.emit('alert', 'Te quedan 2 minutos.'), 13 * 60000);
            setTimeout(() => socket.emit('alert', 'Te queda 1 minuto. Si llegas a los 15 minutos afectaras tu TMO.'), 14 * 60000);
            setTimeout(() => socket.emit('alert', 'Tiempo Expirado. Cliente guardado con exeso de tiempo.'), 15 * 60000);
        });

        socket.on('createTransaction', async (type, pref) => {
            const correl = await Turn.find({ type }).countDocuments() + 1;
            const turn = new Turn({ type, correl, pref, state: "E" }); // E = Esperando, G: Atendiendo || X: Cancelado, A: Atendido, AE: Atendido Exeso
            await turn.save();
            socket.emit('newTransaction', `${type}${addZero(correl)}`);
            console.log(`${type}${addZero(correl)}`);
            getTruns(io, type, pref)
        });

        socket.on('logout', (type, name, isPreference) => {
            users = users.filter(socket => socket.nickname.type != type && socket.nickname.name != name)
            if(isPreference) io.sockets.emit('preferencesConnected', false, type)
        });

        socket.on('atender', async req => { // {_id, type: "C", correl: 1, pref: true}
            const turn = JSON.parse(req)
            await Turn.findByIdAndUpdate(turn._id, new Turn(turn))
        })

        socket.on('getReportData', async () => {
            const turns = await Turn.find()
            socket.emit('reportData', turns)
        });

        socket.on('llamar', (type, username, codigo) => { // {_id, type: "C", correl: 1, pref: true}
            const name = getTableName(type, username)
            console.log("Ticket Numero", codigo, name)
            io.sockets.emit("llamarTicket", codigo, name)
        })

        socket.on('getReportData', () => {
            
        });
    });
};

const getTruns = async (io, type, pref) => {
    const turns = await Turn.find({ type, pref, state: "E" });
    getAllTruns(io)
    io.sockets.emit(`newTurn${type}`, turns, pref)
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