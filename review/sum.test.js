const add=require('./sum');

test('Sum of two given no. is',()=>{
  expect(add(5,5)).toBe(10);
})

test('Sum of two given no. is not',()=>{
  expect(add(5,5)).not.toBe(9);
})