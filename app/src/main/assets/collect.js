(function() {
    test();
})();

function test(){
    var ele = document.getElementById('book_fiction');
    ele.parentNode.removeChild(ele);
    var book_json = getBookInfo();
    window.android.startFunction(book_json);
}

function getBookInfo(){
	var book_item_set = document.getElementsByClassName('item item__book');
	var bookList = new Array();
	for (var i = 0; i < book_item_set.length; i++) {
		var inner = book_item_set[i].children[0];
		var href = inner.href;
		var item = inner.children;
		var book_name = item[1].innerHTML;
		var star = item[2].children[0].children[0].getAttribute("data-rating");
		console.log(href, book_name, star);
		book = new Object();
		book.title = book_name;
		book.url = href;
		book.star = star;
		bookList[i] = book;
	}
	var book_json = JSON.stringify(bookList);
	return book_json;
}