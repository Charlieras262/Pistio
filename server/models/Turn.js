const mongoose = require('mongoose');
const { Schema } = mongoose;

const TurnSchema = new Schema({
    type: { type: String, required: true },
    correl: { type: Number, required: true },
    state: { type: String, required: false, default: 'W' }
});

module.exports = mongoose.model('Turn', TurnSchema);