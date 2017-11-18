var login = View.extend({
    el: '#login',
    uis: {
        announcement: {
            el: '#announcement',
            type: 'window',
            config: {
                title: '公告',
                width: '650px'
            }
        },
        username: {
            el: '#username',
            type: 'textbox',
            config: {
                name: 'yhm',
                emptyText: SYS.dlmPlaceholder,
                onkeydown: function (e) {
                    var event = e || window.event;
                    if (event.htmlEvent.keyCode == 13) {
                        this.uis.loginBtn.mui.doClick();
                    }
                }
            }
        },
        idType: {
            el: '#idType',
            type: 'combobox',
            config: {
                name: 'idType',
                code: 'DM_GY_SFZJLX',
                valueField: 'value',
                textField: 'label',
                value: '201',
                cache: true,
                showNullItem: false,
            }
        },
        idNumber: {
            el: '#idNumber',
            type: 'textbox',
            config: {
                name: 'idNumber',
                onblur: function () {
                    this.setValue(jQuery.trim(this.getValue()));
                },
                onkeydown: function (e) {
                    var event = e || window.event;
                    if (event.htmlEvent.keyCode == 13) {
                        this.uis.loginBtn.mui.doClick();
                    }
                }
            }
        },
        password: {
            el: '#password',
            type: 'password',
            config: {
                name: 'mm',
                autocomplete: 'off',
                onkeydown: function (e) {
                    var event = e || window.event;
                    if (event.htmlEvent.keyCode == 13) {
                        this.uis.loginBtn.mui.doClick();
                    }
                }
            }
        },
        authCode: {
            el: '#authCode',
            type: 'textbox',
            config: {
                name: 'authCode',
                maxLength: 4,
                onkeydown: function (e) {
                    var event = e || window.event;
                    if (event.htmlEvent.keyCode == 13) {
                        this.uis.loginBtn.mui.doClick();
                    }
                },
                onkeyup: function (e) {
                    var value = e.htmlEvent.target.value;
                    var _this = this;
                    if (value.length === 4) {
                        var event = e || window.event;
                        if (event.htmlEvent.keyCode == 13) {
                            return;
                        }
                        $.send({
                            type: 'POST',
                            url: 'captcha/validateCaptcha',
                            data: {
                                captcha: value
                            },
                            success: function (result) {
                                if (result.data == '') {
                                    jQuery('.auth-pass').show();
                                } else {
                                    _this.view.buildErrorMsg([result.data]);
                                }
                            }
                        });
                    } else {
                        jQuery('.msg').html('');
                        jQuery('.auth-pass').hide();
                    }
                }
            }
        },
        redirect_uri: {
            el: '#redirect_uri',
            type: 'hidden',
            config: {
                name: 'redirect_uri',
                value: base.getQueryString('redirect_uri'),
            }
        },
        response_type: {
            el: '#response_type',
            type: 'hidden',
            config: {
                name: 'response_type',
                value: base.getQueryString('response_type'),
            }
        },
        client_id: {
            el: '#client_id',
            type: 'hidden',
            config: {
                name: 'client_id',
                value: base.getQueryString('client_id'),
            }
        },
        sign: {
            el: '#sign',
            type: 'hidden',
            config: {
                name: 'sign',
                value: base.getQueryString('sign'),
            }
        },
        st: {
            el: '#st',
            type: 'hidden',
            config: {
                name: 'st',
                value: base.getQueryString('ST'),
            }
        },
        dllx: {
            el: '#dllx',
            type: 'hidden',
            config: {
                name: 'dllx',
                value: 'yhm'
            }
        },
        loginBtn: {
            el: '#loginBtn',
            type: 'button',
            config: {
                name: 'loginBtn',
                onclick: function () {
                    var _this = this;
                    var valid = _this.view.verifyWhenClickLoginBtn();
                    if (!valid) {
                        return;
                    }
                    var submitData = $("#loginForm").m2j();
                    var password = _this.view.rsaEncry(submitData.mm);
                    submitData.mm = password;
                    // 提交
                    $.send({
                        type: 'POST',
                        url: 'oauth2/login',
                        contentType: 'application/json; charset=UTF-8',
                        data: submitData,
                        dataType: 'json',
                        mask: '#loginBtn',
                        nomsg: true,
                        success: function (result) {
                            _this.setText('正在进入');
                            //跳转
                            window.location = result.data;
                        },
                        failed: function (result) {
                            var msgs = [];
                            msgs.push(result.content);
                            _this.view.buildErrorMsg(msgs);
                            _this.view.refreshCaptcha();
                        }
                    });
                }
            }
        }
    },
    render: function () {

        if ($('.banner-slider').length > 0) {
            $('.banner-slider').slide({
                titCell: '.nav ul',
                mainCell: '.slides ul',
                effect: 'leftLoop',
                autoPlay: true,
                autoPage: true,
                mouseOverStop: false,
                interTime: 8000,
                delayTime: 800,
                vis: 'auto'
            });
        }

        //解决IE8 readonly存在光标问题
        $("input[readOnly][type='text']").attr("UNSELECTABLE", "on");
        $('.js-help').each(function () {
            var t;
            $(this).mouseenter(function () {
                var _this = $(this);
                t = setTimeout(function () {
                    _text = _this.children('.item');
                    _text.animate({
                        width: 80
                    }, 300);
                    _this.children('.icon').css({
                        'background-color': 'rgba(0,0,0,0.6)',
                        'filter': 'progid:DXImageTransform.Microsoft.Gradient(startColorstr=#99000000,endColorstr=#99000000)'
                    });
                }, 100);
            });
            $(this).mouseleave(function () {
                clearTimeout(t);
                _text = $(this).children('.item');
                _text.animate({width: 0}, 300);
                $(this).children('.icon').css({
                    'background-color': 'rgba(34,34,34,0.6)',
                    'filter': 'progid:DXImageTransform.Microsoft.Gradient(startColorstr=#99222222,endColorstr=#99222222)'
                });
            });
        });
        //iFrame 里退出
        if (window.top != window) {
            if (window.top.location.host == window.location.host) {
                var href = window.location.href;
                var requestUrl = href.substr(href.indexOf(0,
                    window.location.search));
                window.top.location.href = requestUrl;
            }
        }
        login.getAnnouncement();

        // 通知公告
        if(SYS.openMsgCenter) {
            //打开消息中心才发送获取通知公告请求
            login._showTzgg();
        }
    },

    verifyWhenClickLoginBtn: function () {
        var password = this.uis.password.mui.getValue();
        if(jQuery("#loginPanel").length > 0 && jQuery("#loginPanel.fn-hide").length == 0){
            var username = this.uis.username.mui.getValue();
            //用户名登陆
            if (username == '' || password == '') {
                this.buildErrorMsg(['用户名或密码不能为空']);
                return false;
            }
        } else {
            var idNumber = this.uis.idNumber.mui.getValue();
            if (this.uis.idType.mui.getValue() == '201') {//为身份证
                if (!base.verifySfzjhm(idNumber)) {
                    this.buildErrorMsg(['请输入正确的15位或18位居民身份证号码']);
                    return false;
                }
            }
            if (idNumber == '' || password == '') {
                this.buildErrorMsg(['身份证件号码或密码不能为空']);
                return false;
            }
        }
        var authCode = this.uis.authCode.mui.getValue();
        if (authCode == '') {
            this.buildErrorMsg(['请输入验证码']);
            return false;
        }
        return true;
    },

    buildErrorMsg: function (msgSummary) {
        var msgUl;
        jQuery('.login-msg-ctn').html('');
        var errorMsg = jQuery('<div class="msg"></div>');
        var msgIcon = jQuery('<i class="msg-icon icon-exclamation-sign"></i>');
        msgUl = jQuery('<ul></ul>');
        errorMsg.append(msgIcon).append(msgUl);
        $.each(msgSummary, function (i, v) {
            var msgSummaryLi = jQuery('<li></li>');
            msgSummaryLi.html('<span class="msg-item">' + v + '</span>');
            msgUl.append(msgSummaryLi);
        });
        jQuery('.login-msg-ctn').append(errorMsg);
    },
    refreshCaptcha: function () {
        $("#img_captcha").attr("src", SYS.ctx +
            "/web/captcha/refreshCaptcha?t=" + Math.random() + "&token=" + this.uis.st.param.token);
        jQuery('.auth-pass').hide();
        this.uis.authCode.mui.setValue("");
    },
    rsaEncry: function (password) {
        var rsaPubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEKu2Fc233FMyxtgQZSS6+b/7rIquYbTWfJM5kOkJDVDUe9UD8WSgj3hpXumLxiK2eUJFutCYRpch4GqplPVejYz/LRb6/Zapu+LrVMbmE0aU8AYfs0uemkVUHkMVnJWi3oUOVUMf3AroZ4UJctwawl2b98suKOwdjTk7Lywb6kwIDAQAB";
        var encrypt = new JSEncrypt();
        encrypt.setPublicKey(rsaPubKey);
        return encrypt.encrypt(password);
    },
    getAnnouncement: function () {
        // var _this = this;
        // $.send({
        //     type: 'POST',
        //     url: 'getAnnouncement',
        //     success: function (result) {
        //         var announcementUI = _this.uis.announcement.mui;
        //         var data = result.data;
        //         announcementUI.setTitle(data.announcementTitle);
        //         $('#announcement').find('p').html(data.announcementContent);
        //         announcementUI.show();
        //     },
        //     failed: function () {
        //     }
        // });
    },
    _showTzgg: function () {
        // $.send({
        //     type: 'POST',
        //     url: 'tzgg/findTzggsPage',
        //     data: {
        //         pageIndex: 0,
        //         pageSize: 2
        //     },
        //     success: function (result) {
        //         if (result != null && result.data != null && result.data.tzggs.length > 0) {
        //             var _count = result.data.totalNum;
        //             $('#tzggCtn').show();
        //             if (_count > 2) {
        //                 $('#tzggMore').show();
        //             }
        //             template.helper('dateFormat', function (date, format) {
        //                 return $.formatDate(date, format);
        //             });
        //             $('#tzggList').html(template.render($.trim($('#tzggTpl').html()))({
        //                 list: result.data.tzggs
        //             }));
        //         }
        //     }
        // });
    }
});


function switchLoginWay(obj) {
    var jQObj = jQuery(obj);
    jQObj.addClass('active');
    jQObj.next().removeClass('active');
    jQuery('#loginPanel').removeClass('fn-hide');
    $('#idloginPanel').addClass('fn-hide');
    resetLogin();
}
function switchIdLoginWay(obj) {
    var jQObj = jQuery(obj);
    jQObj.addClass('active');
    jQObj.prev().removeClass('active');
    jQuery('#loginPanel').addClass('fn-hide');
    $('#idloginPanel').removeClass('fn-hide');
    resetLogin();
}
function resetLogin() {
    login.uis.username.mui.setValue('');
    login.uis.idNumber.mui.setValue('');
    login.uis.password.mui.setValue('');
    login.uis.authCode.mui.setValue('');
    if (typeof(login) != "undefined") {
        login.uis.idType.mui.setValue('201');
    }
    jQuery('.msg').html('');
    jQuery('.auth-pass').hide();
}