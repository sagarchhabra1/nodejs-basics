const fetch = require('./promise');

test("promise testing", () => {
    return fetch().then((data) => {
        expect(data).toBe('hello');
    })

})