const puppeteer = require('puppeteer');

run().then(() => console.log('Done')).catch(error => console.log(error));

async function run() {

  const browser = await puppeteer.launch({ headless: false });

  const page = await browser.newPage();
  await page.goto('https://google.com/');

  await new Promise(resolve => setTimeout(resolve, 5000));
  await browser.close();
}
#this is the code
