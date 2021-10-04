const fetch = require('./fetchdata');

test("callback testing", done => {
    function callback(data) {

        try {
            expect(data).toBe('hello world');
            done();
        } catch (error) {
            done(error);
        }
    }
    fetch(callback)
})