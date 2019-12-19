
module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:2048',
                changeOrigin: true,
                ws: true,
                secure: false,
                pathRewrite: {
                  '^/api': ''
                }
            }
        },
    },
    filenameHashing: false
}