var index = {
    init : function () {
        const that = this;
        $('#btn-convert').on('click', function () {
            that.convert();
        });
        $('#url').on('keypress', function (e) {
            if(e.which === 13) {
                that.convert();
            }
        })
    },
    convert : function () {
        const url = $('#url').val();
        if(url === null || url === '') {
            showEmptyValue();
            return;
        }

        const data = {
            url: url
        };

        $.ajax({
            type: 'POST',
            url: '/convert-url',
            dataType: 'json',
            contentType:'application/json; charset=utf-8',
            data: JSON.stringify(data),
            async: false
        }).done(function(result) {
            if(result.code !== 0) {
                showFailResult(result);
            }else {
                showSuccessResult(result);
            }
        }).fail(function (error) {
            alert(error);
        });
    }

};
index.init();

function showEmptyValue() {
    bootbox.alert({
        message: 'URL이 비어있습니다. URL을 입력해주세요.',
        callback: function() {
            $('#url').focus();
            $('#url').val('');
        }
    });
}


function showFailResult(result) {
    bootbox.alert({
        message: result.message + ' 오류코드 = [' + result.code + ']',
        callback: function() {
            $('#url').val('');
            $('#url').focus();

            return;
        }
    });
}

function showSuccessResult(result) {
    const body = result.body;

    if(body.urlType === 'ORIGINAL_URL') {
        showOriginalUrlResult(body);
    } else {
        bootbox.alert({
            message: body.url + '로 이동합니다.',
            scrollable: true,
            callback: function() {
                location.href = '/redirect-original-url?redirectUrl=' + encodeURIComponent(body.url);
            }
        });
    }
}

function showOriginalUrlResult(body) {
    const message = body.newUrl ? '새로운 단축 URL이 발행되었습니다. ' : '기존에 만들어진 단축 URL이 있습니다. ';

    bootbox.alert({
        message: message + body.url,
        callback: function() {
            $('#url').val('');
            $('#url').focus();
        }
    });

}