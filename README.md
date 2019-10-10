# BarSploit
Generate barcodes to exploit kiosks.

---

Currently in development. Not all features are implemented, and there are bugs. Use at your own risk.

Unimplemented:
- Built-in configuration codes
- Exploit presets

## Usage

- Create a barcode group
- Create a new barcode
- Tap 'Add'
- Select the type of data (text or hexadecimal bytes)
- Type/paste your data
- Tap the checkmark for that row
- Once you've added all your data, tap the checkmark in the top bar
- Add more barcodes
- Print the barcodes to PDF (or a real printer) using the print button
- Save the new barcode group by tapping the checkmark in the top bar

Only some scanners can read from phone screens. You may need to print the barcodes or display them on an e-ink display (i.e. a Kindle).

If a Kindle is connected with a USB OTG cable, you can save a page of barcodes directly to the Kindle.

To create a configuration code, you will currently need to create a Code 128 barcode composed of an 0xF3 byte and a configuration code string.
Look up the manual for the barcode scanner model, and type the configuration code into the app. Use a barcode scanning app such as Binary Eye if the manual only shows the barcode but not the configuration number/string.

Examples of exploits include:

- Sending Alt+Space, then minimising or moving the window to reveal the desktop
- Sending Win+R and starting cmd
- Sending Ctrl+Alt+Delete, then using Task Manager to start a shell

To send some keys such as the Windows key, you might need to configure something specifically for that key - for example, a replacement character. The barcode scanner manual should have a list of codes for special keys that can be used with certain configuration options.

## Disclaimer

Don't do crimes.

If you don't have permission to test a kiosk, then don't test it!
