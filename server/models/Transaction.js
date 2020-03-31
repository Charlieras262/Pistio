const mongoose = require('mongoose');
const { Schema } = mongoose;

const TransactionSchema = new Schema({
    type: {type: String, required: true},
    correl: {type: Number, required: true},
    support: {type: Schema.ObjectId, ref: 'User', required: false },
    hStart: {type: Date, required: true},
    hEnd: {type: Date, required: false}
});

module.exports = mongoose.model('Transaction', TransactionSchema);