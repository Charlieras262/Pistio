const mongoose = require('mongoose');
const { Schema } = mongoose;

const SupportSchema = new Schema({
    _id: { type: Schema.ObjectId, ref: 'User', required: true },
    type: {type: String, required: true},
    clients: [{ type: Schema.ObjectId, ref: 'Client', required: false }]
});

module.exports = mongoose.model('Support', SupportSchema);