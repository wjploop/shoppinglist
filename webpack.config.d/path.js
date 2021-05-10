config.resolve.alias = {
    "crypto": false,
}
config.devServer = Object.assign(
    {},
    config.devServer || {},
    { open: false }
)