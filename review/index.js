console.log("Hello World");

//DATA TYPES
var num1 = 20;
var num2 = 10;
console.log(num2);
console.log(num1 + num2);

var str1 = "This is a String";
var str2 = "This is also a String";
console.log(str1);

var a = true;
var b = false;
console.log(a, b);

var und;
console.log(und);

var g = null;
console.log(g);

// ARRAYS
var arr = [1, 2, "golu", 4, 5];
console.log(arr);
console.log(arr[0]);
console.log(arr[4]);

//OBJECTS
var marks = {
    sahil: 55,
    shubham: 45,
    harry: 80
}
console.log(marks);

// OPERATORS
var a = 30;
var b = 20;
console.log("The value of a + b is", a + b);
console.log("The value of a - b is", a - b);
console.log("The value of a * b is", a * b);
console.log("The value of a / b is", a / b);

var c = b;
//c+=2;
c -= 2;
//c*=2;
//c/=2;
console.log(c);

//Spread operator
var arr = [1, 2, 3];
var arr2 = [4, 5];
arr = [...arr, ...arr2];
console.log(arr);

//concat
var arr = [1, 2, 3];
var arr2 = [4, 5];
arr = arr.concat(arr2);
console.log(arr);

//Splice
var arr = [1, 2, 3];
var arr2 = arr;
console.log(arr2);

//Push
var arr = [1, 2, 3];
var arr2 = arr;
arr2.push(4);
console.log(arr2);

//Expand
var arr = [1, 2, 3];
var arr2 = ["a", "b", arr];
console.log(arr2);

var arr = [1, 2, 3];
var arr2 = ["a", "b", ...arr];
console.log(arr2);

//Math
console.log(Math.min(1, 2, 3, 4));

//Destructuring 
var a, b;
[a, b] = [34, 56];
console.log(a, b);

[a, b, c, ...d] = [1, 2, 3, 4, 5, 6, 8];
console.log(a);
console.log(b);
console.log(c);
console.log(d);

//Array Destr
({ a, b, c, ...d } = { a: 35, b: 44, c: 55, d: 99, e: 88 })
console.log(a, b, c, d);

const fruits = ['Apple', 'Banana', 'Mangoes'];
[a, b, c] = fruits;
console.log(a, b, c);

//Object Destr
const mark = {
    sub: "Maths",
    age: 16,
    name: "Rahul"
}
const { sub, age, name } = mark;
console.log(sub, age, name);

//Function

const sagar = function () {
    console.log("This is the best person ever");
}
sagar();

//Arrow Function
const anuj = () => {
    console.log("This is the best person ever");
}
anuj();

const sahil = () => "Good Morning";
console.log(sahil());

const ajay = () => ({ name: "God" });
console.log(ajay());

//Callback Function


function sum(a, b) {
    return a + b;
}

function diff(a, b) {
    return a - b;

}
const fg = (fx, a, b) => {
    return fx(a, b);
}
console.log(fg(sum, 4, 5));

console.log(fg(diff, 4, 5));


