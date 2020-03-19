const mongoose = require('mongoose');
const { Schema } = mongoose;

const ClientSchema = new Schema({
    _id: { type: Schema.ObjectId, ref: 'User', required: true }
});

module.exports = mongoose.model('Client', ClientSchema);