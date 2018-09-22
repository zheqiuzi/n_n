// var webpack=require("webpack");
// const { WebPlugin } = require('web-webpack-plugin');
// var webpack = require('webpack')
//
// module.exports={
//     resolve: {
//         alias: {
//             // 'vue$': 'vue/dist/vue.esm.js' // 用 webpack 1 时需用 'vue/dist/vue.common.js'
//             'vue': 'vue/dist/vue.js'
//         }
//     },
//
//     loaders:[
//         {test:/\.vue$/, loader:'vue-loader'},
//         {test:/\.js$/, loader:'babel-loader', exclude:/node_modules/}
//     ],
//
//     entry:{
//         gamer:"./static/js/gamer.js"
//     },
//     output: {
//         path: __dirname + '/dist/',
//         filename: "[name].bundle.js",
//         publicPath: '/'
//     },
//     plugins:[
//
//         new webpack.LoaderOptionsPlugin({
//             options: {
//                 babel:{
//                     presets:['es2015'],
//                     plugins:['transform-runtime']
//                 }
//             }
//         }),
//
//         new webpack({
//             filename:"html/gamer.html",
//             requires:['gamer']
//         })
//     ]
// }
var webpack = require('webpack');
const { WebPlugin } = require('web-webpack-plugin');
module.exports={
    devtool: "source-map",
    entry:{
        gamer:"./static/js/gamer.js"
    },
    output: {
        path: __dirname + '/dist/',
        filename: "[name].bundle.js",
        // publicPath: './dist/'
    },
    resolve: {
        alias: {
            'vue': 'vue/dist/vue.js',
            "jquery":"jquery/dist/jquery.js"
        }
    },
    module:{
        loaders:[
            {test:/\.vue$/, loader:'vue-loader'},
            {test:/\.js$/, loader:'babel-loader', exclude:/node_modules/}
        ]
    },
    plugins: [
        new webpack.LoaderOptionsPlugin({
            options: {
                babel:{
                    presets:['es2015'],
                    plugins:['transform-runtime']
                }
            }
        }),
        new WebPlugin({
            filename:"html/gamer.html",
            requires:['gamer'],
            template:"html/gamer.html"

        }),
        // new WebPlugin({
        //     filename:"index.html",
        //     requires:['gamer']
        // })
    ]
};
