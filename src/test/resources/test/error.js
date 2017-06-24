define("gg-site/query", ["gg-site/util", "gg-site/express-company-list", "gg-site/mtop", "gg-site/addon"], function (e) {
    var o = e("gg-site/express-company-list"), t = e("gg-site/util"), i = e("gg-site/mtop");
    e("gg-site/addon");
    var a = t.cookies, r = t.getQueryString("cptype") || "all", c = t.getQueryString("togoList"), n = "queryByCode.htm?cptype=" + r + "&cpcode=", s = "_cngg_website_search_history_", l = {
        init: function () {
            this._goldlog(), this._initNav(), this._bindSearchBtn(), this._initSearchHistory(), this._bindClickHistory(), this._bindDeleteHistory(), this._initExpressCompanyList(), this._bindExpressCompanyList()
        }, _goldlog: function () {
            function e(e) {
                if ("wuliu" === e)return void goldlog.record("/cn.17.30.21", "", "", "H46777383");
                if ("outerkd" === e)return void goldlog.record("/cn.17.30.22", "", "", "H46777405");
                switch (c || (c = 0), c = parseInt(c)) {
                    case 1:
                        goldlog.record("/cn.17.30.1", "", "", "H46747592");
                        break;
                    case 2:
                        goldlog.record("/cn.17.30.2", "", "", "H46747614");
                        break;
                    case 3:
                        goldlog.record("/cn.17.30.3", "", "", "H46747615");
                        break;
                    case 4:
                        goldlog.record("/cn.17.30.4", "", "", "H46747616");
                        break;
                    case 5:
                        goldlog.record("/cn.17.30.5", "", "", "H46747617");
                        break;
                    case 6:
                        goldlog.record("/cn.17.30.6", "", "", "H46747618");
                        break;
                    case 7:
                        goldlog.record("/cn.17.30.7", "", "", "H46747619");
                        break;
                    case 8:
                        goldlog.record("/cn.17.30.8", "", "", "H46747620");
                        break;
                    case 9:
                        goldlog.record("/cn.17.30.9", "", "", "H46747621");
                        break;
                    case 10:
                        goldlog.record("/cn.17.30.10", "", "", "H46777382");
                        break;
                    case 11:
                        goldlog.record("/cn.17.30.11", "", "", "H46777383");
                        break;
                    case 12:
                        goldlog.record("/cn.17.30.12", "", "", "H46777405");
                        break;
                    case 13:
                        goldlog.record("/cn.17.30.13", "", "", "H46777406");
                        break;
                    case 14:
                        goldlog.record("/cn.17.30.14", "", "", "H46777407");
                        break;
                    case 15:
                        goldlog.record("/cn.17.30.15", "", "", "H46777408");
                        break;
                    case 16:
                        goldlog.record("/cn.17.30.16", "", "", "H46777409");
                        break;
                    case 17:
                        goldlog.record("/cn.17.30.17", "", "", "H46777410");
                        break;
                    case 18:
                        goldlog.record("/cn.17.30.18", "", "", "H46777411");
                        break;
                    case 19:
                        goldlog.record("/cn.17.30.19", "", "", "H46777412");
                        break;
                    case 20:
                        goldlog.record("/cn.17.30.20", "", "", "H46777382");
                        break;
                    default:
                        goldlog.record("/cn.17.30.30", "", "", "H46777382")
                }
            }

            !function () {
                window.goldlog ? e(r) : setTimeout(arguments.callee, 200)
            }()
        }, _initNav: function () {
            var e = ($(".nav"), $(".express-company-container .title span"));
            switch (r) {
                case"wuliu":
                    t.selectNav("query-wuliu"), document.title = "\u67e5\u7269\u6d41", e.text("\u7269\u6d41\u516c\u53f8\u5927\u5168");
                    break;
                case"outerkd":
                    t.selectNav("query-outerkd"), document.title = "\u67e5\u56fd\u9645\u5feb\u9012", e.text("\u56fd\u9645\u5feb\u9012\u516c\u53f8\u5927\u5168");
                    break;
                default:
                    t.selectNav("query")
            }
        }, _bindSearchBtn: function () {
            var e = this;
            $("#J_SearchBtn").on("click", function () {
                if (!$(".search-container").hasClass("loading")) {
                    var o = $.trim($("#J_SearchInput").val());
                    if ("" === o)return;
                    e._handleSearch(o)
                }
            }), $("#J_SearchInput").on("click", function () {
                $("#J_HistoryList").show()
            }), $("#J_SearchInput").on("blur", function () {
                setTimeout(function () {
                    $("#J_HistoryList").hide()
                }, 200)
            })
        }, _showHistoryList: function (e) {
            var o = "";
            e.forEach(function (e) {
                o += "<ol>" + e + "</ol>"
            }), $("#J_HistoryList").html(o)
        }, _initSearchHistory: function () {
            var e = a.getCookie(s), o = e ? JSON.parse(e) : [], t = "", i = o.length;
            $.each(o, function (e, o) {
                var a = e === i - 1 ? "last" : "";
                t += '<li class="' + a + '" data-id="' + o.mailNo + '">   <a class="mailNo" href="javascript:void(0);">' + o.mailNo + '       <span class="J_DeleteHistory del" data-id="' + o.mailNo + "," + o.cpCode + '"><i class="del-icon"></i></span>   </a></li>'
            }), $("#J_HistoryList").html(t)
        }, _bindClickHistory: function () {
            var e = this;
            $("#J_HistoryList").on("click", "li", function (o) {
                var t = $(o.target), i = $(o.currentTarget);
                if (!t.hasClass("J_DeleteHistory") && !t.parents().hasClass("J_DeleteHistory")) {
                    var a = i.attr("data-id");
                    $("#J_SearchInput").val(a).focus(), e._handleSearch(a)
                }
            })
        }, _bindDeleteHistory: function () {
            $("#J_HistoryList").on("click", ".J_DeleteHistory", function (e) {
                var o = $(e.currentTarget), t = o.attr("data-id").split(","), i = t[0], r = t[1], c = a.getCookie(s), n = c ? JSON.parse(c) : [];
                $.each(n, function (e, o) {
                    return i == o.mailNo && r == o.cpCode ? (n.splice(e, 1), !1) : void 0
                }), a.setCookie(s, JSON.stringify(n), 365, "/"), o.parents("li").remove()
            })
        }, _initExpressCompanyList: function () {
            o.renderTo("#J_ExpressCompanyList")
        }, _bindExpressCompanyList: function () {
            $("#J_ExpressCompanyList").on("click", "a", function (e) {
                var o = $(e.currentTarget), t = o.attr("data-cpcode"), i = $.trim($("#J_SearchInput").val());
                location.href = n + t + "&mailNo=" + encodeURIComponent(i)
            })
        }, _handleSearch: function (e) {
            this.loading(), this._requestPackage(e)
        }, _requestPackage: function (e) {
            function o(o) {
                c.unloading();
                var i = o.data;
                if (i) {
                    var r = i.cpCompanyInfo || {}, n = i.transitList || [];
                    n = n.reverse(), t(r), a(n), c._setHistory(r.companyCode, e), c._initSearchHistory()
                }
            }

            function t(e) {
                $(".cp-logo img").attr("src", e.iconUrl102x38), $(".cp-name").html(e.companyName);
                var o = $(".cp-link");
                o.attr("href", "http://" + e.webUrl), o.html(e.webUrl), $(".cp-phone label").html(e.serviceTel), $(".cp-container").show()
            }

            function a(e) {
                var o = "";
                $.each(e, function (e, t) {
                    o += 0 == e ? '<li class="latest">' : "<li>", o += '   <span class="date">' + t.time + '</span>   <span class="text">' + t.message + "</span></li>"
                }), $("#J_SearchTimeout").hide(), 0 == e.length ? ($("#J_SearchNoRecord").show(), $(".package-container").hide()) : ($("#J_PackageDetail").html(o), $(".package-container").show(), $("#J_SearchNoRecord").hide())
            }

            function r() {
                c.unloading(), $(".cp-container").hide(), $(".package-container").hide(), $("#J_SearchNoRecord").show()
            }

            var c = this;
            lib.mtop.request({
                api: i.queryLogisticPackageByMailNo,
                v: "1.0",
                data: {mailNo: e},
                timeout: 5e3,
                type: "GET",
                dataType: "jsonp",
                isSec: 0,
                ecode: 0
            }, o, r)
        }, _setHistory: function (e, o) {
            var t = {cpCode: e, mailNo: o}, i = a.getCookie(s), r = JSON.parse(i) || [], c = !1;
            $.each(r, function (e, o) {
                return t.mailNo == o.mailNo && t.cpCode == o.cpCode ? (r.splice(e, 1), !1) : void 0
            }), r.unshift(t), c || r.length > 10 && r.pop(), a.setCookie(s, JSON.stringify(r), 365, "/")
        }, loading: function () {
            $(".search-container").addClass("loading")
        }, unloading: function () {
            $(".search-container").removeClass("loading")
        }
    };
    return l.init(), l
});