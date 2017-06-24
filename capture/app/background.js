// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// chrome.cookies.onChanged.addListener(function(info) {
//   console.log("onChanged" + JSON.stringify(info));
// });


var bkg = chrome.extension.getBackgroundPage();




function focusOrCreateTab(url) {
  chrome.windows.getAll({"populate":true}, function(windows) {
    var existing_tab = null;
    for (var i in windows) {
      var tabs = windows[i].tabs;
      for (var j in tabs) {
        var tab = tabs[j];
        if (tab.url == url) {
          existing_tab = tab;
          break;
        }
      }
    }
    if (existing_tab) {
      chrome.tabs.update(existing_tab.id, {"selected":true});
    } else {
      chrome.tabs.create({"url":url, "selected":true});
    }
  });
}


// returns an edited header object with retained postman headers
//
// function onBeforeSendHeaders(details) {
//   var hasRestrictedHeader = _.find(details.requestHeaders, function(headerObject) {
//         return headerObject.name.indexOf("Postman-") === 0;
//       }),
//       requestHeaders = details.requestHeaders,
//       index,
//       name,
//       prefix = "Postman-",
//       prefixLength = prefix.length,
//       newHeaders = [],                // array to hold all headers sent by postman
//       n,
//       os = [],
//       ds = [],
//       i = 0, j = 0,
//       term;
//
//   // runs only if a header with a Postman- prefix is present
//   if (hasRestrictedHeader) {
//
//     for(i = 0, len = requestHeaders.length; i < len; i++) {
//       name = requestHeaders[i].name;
//
//       // for all headers that are being sent by Postman
//       if (name.search(prefix) === 0 && name !== "Postman-Token") {
//         n = requestHeaders[i].name.substr(prefixLength);
//
//         // push them in newHeaders
//         newHeaders.push({
//           "name": n,
//           "value": requestHeaders[i].value
//         });
//
//         var term = prefix + n;
//
//         ds.push(arrayObjectIndexOf(requestHeaders, term, "name") );
//       }
//     }
//
//     // retains the postman headers that are repeated
//     for(j = 0; j < ds.length; j++) {
//       requestHeaders.splice(ds[j], 1);
//     }
//
//     i = 0;
//
//     if (requestHeaders[i]) {
//       while(requestHeaders[i]) {
//         name = requestHeaders[i].name;
//         if (name.search(prefix) === 0 && name !== "Postman-Token") {
//           requestHeaders.splice(i, 1);
//           i--;
//         }
//
//         i++;
//       }
//     }
//
//     for(var k = 0; k < newHeaders.length; k++) {
//       requestHeaders.push(newHeaders[k]);
//     }
//
//     delete requestCache[details.requestId];
//   }
//
//   return {requestHeaders: requestHeaders};
// }


function onDisconnected() {
  bkg.console.log("Failed to connect: " + chrome.runtime.lastError.message);
  port = null;
}

function onBeforeSendHeaders(details) {
  var hostName = "com.myjo.ordercat";
  var requestHeaders = details.requestHeaders;
  if(details.url.indexOf("://wuliu.taobao.com/user/`.do") != -1){
    bkg.console.log(details);
    bkg.console.log(requestHeaders);
    var port = chrome.runtime.connectNative(hostName)

    port.onDisconnect.addListener(onDisconnected);

    var message = {};

    for(var i =0;i<requestHeaders.length ; i++){
        message[requestHeaders[i].name] = requestHeaders[i].value;
    }
    port.postMessage(message);
  }




  //https://wuliu.taobao.com
  return {requestHeaders: requestHeaders}
}

chrome.webRequest.onBeforeSendHeaders.addListener(onBeforeSendHeaders,
    { urls: ["<all_urls>"] },
    [ "blocking", "requestHeaders" ]
);

// chrome.webRequest.onBeforeRequest.addListener(
//     function(details) {
//       return {cancel: details.url.indexOf("://www.baidu.com/") != -1};
//     },
//     {urls: ["<all_urls>"]},
//     ["blocking"]);




chrome.browserAction.onClicked.addListener(function(tab) {
  var manager_url = chrome.extension.getURL("main.html");
  focusOrCreateTab(manager_url);
});
