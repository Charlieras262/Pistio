const express = require('express')
const morgan = require('morgan')

const app = express()

//Setting
app.set("port", process.env.PORT || 8080)

//MiddleWares
app.use(morgan('dev'))
app.use(express.json())

// Starting Server
var server = require('http').Server(app);
const io = require('socket.io')(server);
server.listen(app.get('port'), () => {
    console.log(`Express server listening on port ${app.get('port')}`);
});

require('./websocket/socket.io')(io);

module.exports = app;