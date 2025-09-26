const path = require('path');
const webpack = require('webpack');
const dotenv = require('dotenv');

dotenv.config({
  path: './.env',
});

module.exports = {
  target: 'node',
  entry: './src/app.js',
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: 'server.js',
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.PORT': JSON.stringify(process.env.PORT),
      'process.env.SLACK_API_TOKEN': JSON.stringify(
        process.env.SLACK_API_TOKEN,
      ),
    }),
  ],
  stats: {
    errorDetails: true,
  },
};
