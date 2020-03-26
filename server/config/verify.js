const User = require('../models/User');

module.exports.createAdminUser = () => {
    User.getUserByUser('Admin', (err, user) => {
        const userData = {
            nombre: 'Admin',
            apellido: 'Admin',
            usuario: 'Admin',
            pass: 'Admin1234',
            type: 'A'
          }
        if (err) throw err;
        if (!user) {
            console.log('Usuario Administrador no creado.');
            console.log('Creando...');
            new User(userData).save()
            console.log('Usuario Administrador Creado');
        } else {
            console.log('El usuario Administrador ya esta creado.');
        }
    });
}