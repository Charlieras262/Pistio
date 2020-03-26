const mongoose = require('mongoose');
const { Schema } = mongoose;

// En este esquema se establece el modelo del Usuario
const UserSchema = new Schema({
    nombre: { type: String, required: true },
    apellido: { type: String, required: true },
    usuario: { type: String, required: true, unique: true },
    pass: { type: String, required: true },
    type: { type: String, required: true, default: 'S'}
});

// Se exporta el esquema como un modelo para utilizarlo en el controllador
const User = module.exports = mongoose.model('User', UserSchema);

module.exports.getUserByUser = (username, callback) => {
    const query = {
        usuario: username
    }
    User.findOne(query, callback);
}

module.exports.getUserByUserR = (username, callback) => {
    const query = { username: { $regex: '^' + username } }
    User.find(query, callback);
}