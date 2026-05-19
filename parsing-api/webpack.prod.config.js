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
  // Playwright는 native binary 경로/dynamic require를 사용하므로
  // 번들에 포함시키지 않고 런타임 node_modules에서 require 한다.
  // 배포 시 dist/와 함께 node_modules/playwright(+playwright-core)를 포함시킬 것.
  externals: {
    playwright: 'commonjs playwright',
    'playwright-core': 'commonjs playwright-core',
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
