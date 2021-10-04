function promise()
{
    return new Promise((resolve,reject)=>{
        resolve("hello");
    })
}

module.exports=promise;