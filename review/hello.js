function hello()
{
    return new Promise((resolve,reject)=>{
        resolve("done");
    })
}

module.exports=hello;