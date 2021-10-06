const hello=require('./hello');

test("async function",async ()=>{
    const data=await hello();
    expect(data).toBe("done");
})