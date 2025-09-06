function previewImage(event) {
    const input = event.target;
    const reader = new FileReader();

    reader.onload = function () {
        const preview = document.getElementById('img-preview');
        preview.src = reader.result;
    };

    if (input.files[0]) {
        reader.readAsDataURL(input.files[0]);
    }
}
function deleteRecord(id) {
    if (confirm("本当に削除しますか？")) { // 確認ダイアログ
        fetch(`/mansion/delete?id=${id}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    alert("削除しました。");
                    location.href = '/mansion/home'; // リダイレクト
                } else {
                    alert("削除に失敗しました。");
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("エラーが発生しました。");
            });
    }
}
