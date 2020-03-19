const mongoose = require('mongoose');
const { Schema } = mongoose;

const TransactionSchema = new Schema({
    type: {type: String, required: true},
    support: {type: Schema.ObjectId, ref: 'Support', required: true },
    client: {type: Schema.ObjectId, ref: 'Client', required: true },
    hStart: {type: Date, required: true},
    hEnd: {type: Date, required: false},
    state: {type: String, required: false, default: 'WAITING'}
});

module.exports = mongoose.model('Transaction', TransactionSchema);