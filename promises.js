var error = function(){
      
    
    console.log("Geek is very sad!");
}
     
var success = function(){
      
   
    console.log("Geek is optimistic, "
        + "thus becomes successful");
}
     
var caller = function(status) {
    return new Promise(function(resolve, reject) {
        if(status === 'Happy') {
              
            
          resolve();
        }
        else {
           
            
            reject();
        }
    });
};
  

caller('Happy').then(success).catch(error);
  

caller('Sad').then(success).catch(error);