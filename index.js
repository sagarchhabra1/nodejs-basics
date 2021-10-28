const puppeteer = require('puppeteer');


(async () => {

  const browser = await puppeteer.launch({ headless: false });

  const page = await browser.newPage();
  await page.goto('https://www.youtube.com/');
  await browser.waitForTarget(() => false);

  await browser.close();
})();