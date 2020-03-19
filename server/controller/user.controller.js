const User = require('../models/User');
const generalValidations = require('./validations/general.validation');
const UserController = {};
const Validations = require('./validations/general.validation');
const config = require('../config/database');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');

/**
 * Este metodo obtiene la informacion del perfil de un usuario
 * @param {*} req es el requerimiento que envia el front end al servidor.
 * @param {*} res es la respuesta que envia el servidor al front end.
 * @return {JSON} un json con un estado, un mensaje
 * y un usuario.
 */
UserController.getProfile = async (req, res) => {
    const user = await User.find();
    if (user.length === 0) res.json({ success: false, msg: 'Users not found' });
    else res.json({ success: true, msg: 'Users found', user });
};

/**
 * Este metodo obtiene un usuario filtrado por id
 * @param {*} req es el requerimiento que envia el front end al servidor.
 * @param {*} res es la respuesta que envia el servidor al front end.
 * @return {JSON} un json con un estado, un mensaje y un usuario si existe.
 */
UserController.getUser = async (req, res) => {
    const id = req.params.id;
    const user = await User.findById(id);
    if (!user) res.json({ success: false, msg: 'User not found' });
    else res.json({ success: true, msg: 'User found', user });
};


/**
 * Este medotodo guarda un usuario en la base de datos
 * @param {*} req es el requerimiento que envia el front end al servidor.
 * @param {*} res es la respuesta que envia el servidor al front end.
 * @return {JSON} un json con un estado, un mensaje.
 */
UserController.createUser = async (req, res) => {

    var hash = bcrypt.hashSync(req.body.password, 10);
    const userModelReq = {
        username: req.body.user,
        email: req.body.email,
        password: hash,
        valCode: req.body.valCode
    }
    const authRes = valEmail(userModelReq.email);
    if(userModelReq.valCode) var addInfo = await valCode(userModelReq.valCode.toString());
    if (authRes.success) {
        User.findOne({ email: userModelReq.email }).countDocuments((err, number) => {
            if (number !== 0) {
                res.json({ success: false, msg: 'emailAlreadyRegistered', node: 'email' });
            } else {
                if (req.body.type) {
                    const user = new User({
                        name: req.body.name,
                        lastname: req.body.lastname,
                        username: req.body.user,
                        email: req.body.email,
                        password: hash,
                        type: req.body.type
                    })
                    user.save();
                    res.json({ success: true, msg: 'userCreated' });
                } else {
                    if (!addInfo.success) {
                        res.json(addInfo);
                    } else {
                        const user = new User({
                            name: addInfo.user.name,
                            lastname: addInfo.user.lastname,
                            username: req.body.username,
                            email: req.body.email,
                            password: hash,
                            val_code: userModelReq.valCode,
                            type: addInfo.user.type
                        })
                        user.save();
                        res.json({ success: true, msg: 'userCreated' });
                    }
                }
            }
        });
    } else {
        res.json(authRes);
    }

};

/**
 * Este metodo actualiza la informacion de un usuario en la base de datos
 * @param {*} req es el requerimiento que envia el front end al servidor.
 * @param {*} res es la respuesta que envia el servidor al front end.
 * @return {JSON} un json con un estado, un mensaje.
 */
UserController.updateUser = async (req, res) => {
    const user = new User(req.body);
    const authRes = valEmail(user.email);;
    if (authRes.success) {
        await User.findByIdAndUpdate(req.params.id, user);
        res.json({ success: true, msg: 'User updated' });
    } else {
        res.json(authRes);
    }
};

/**
 * Este metodo elimina a un usuario de la base de datos
 * @param {*} req es el requerimiento que envia el front end al servidor.
 * @param {*} res es la respuesta que envia el servidor al front end.
 * @return {JSON} un json con un estado, un mensaje y un usuario si existe.
 */
UserController.deleteUser = async (req, res) => {
    await User.findByIdAndDelete(req.params.id);
    res.json({ success: true, msg: 'User deleted' });
};

var valEmail = (email) => {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    if (!re.test(email)) {
        return {
            success: false,
            msg: 'emailFormat',
            node: 'email'
        };
    } else {
        return {
            success: true,
            msg: 'Email Valid'
        };
    }
}

UserController.authUserInfo = (req, res) => {
    const userModel = {
        email: req.body.email,
        password: req.body.password
    }
    var emailR = Validations.isFilled(userModel.email, "Email");
    var passR = Validations.isFilled(userModel.password, "Password");
    if (!emailR.success) return res.json(emailR);
    emailR = valEmail(userModel.email);
    if (!emailR.success) return res.json(emailR);
    if (!passR.success) return res.json(passR);
    User.getUserByEmail(userModel.email, (err, user) => {
        if (err) throw err;
        if (!user) {
            res.json({
                success: false,
                msg: 'user404',
                node: 'email'
            });
        } else {
            var userJSON = JSON.parse(JSON.stringify(user));
            var hash = userJSON.password;
            var match = bcrypt.compareSync(userModel.password, hash);
            if (match) {
                const token = jwt.sign(user.toJSON(), config.secret, {
                    expiresIn: 604800 // 1 week
                });
                
                res.json({
                    success: true,
                    msg: 'Login success',
                    token: 'JWT ' + token,
                    user: {
                        username: userJSON.username,
                        email: userJSON.email,
                        type: userJSON.type,
                        code: userJSON.user_type_id
                    }
                });
            } else {
                res.json({
                    success: false,
                    msg: 'wrongPass',
                    node: 'password'
                });
            }
        }
    });
}

// Se exporta el controllador para poder utilizarlo en las rutas
module.exports = UserController;