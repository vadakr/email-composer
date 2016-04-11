# Email Composer with Attachments Plugin #

Forked from jcjee/email-composer (Thank you!)

This fork addresses Android specific bugs that prevented one from using base64 attachments (see the attachmentData parameter)

It also adds 2 new parameters to the existing interface - 
1. dontUseContentProviderOnAndroid
2. folderNameOnAndroid.


**Callable interface:**
```
	window.plugins.emailComposer.showEmailComposerWithCallback(
		callback, //a js function that will receive return parameter from the plugin
		subject, //a string representing the subject of the email; can be null
		body, //a string representing the email body (could be HTML code, in this case set **isHtml** to **true**); can be null
		toRecipients, //a js array containing all the email addresses for TO field; can be null/empty
		ccRecipients, //a js array containing all the email addresses for CC field; can be null/empty
		bccRecipients, //a js array containing all the email addresses for BCC field; can be null/empty
		isHtml, //a bool value indicating if the body is HTML or plain text
		attachments, //a js array containing all full paths to the files you want to attach; can be null/empty
		attachmentsData, //a js array of fileName-fileData array pairs, e.g. [['filename1','base64data1'],['filename2','base64data2']]
		dontUseContentProviderOnAndroid, //an optional boolean to indicate whether the email content provider (Android only) should **NOT** be used
		folderNameOnAndroid //an optional folder name (Android only) created on external storage/Downloads when 'dontUseContentProviderOnAndroid'
	);						//is in effect. If not specified, a folder called 'com_ecosysmgmt_cordova_plugins_EmailComposer' is created
```


## License ##

[The MIT License (MIT)](http://www.opensource.org/licenses/mit-license.html)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
