window.addEventListener('load', initAll);

function initAll()
{
    // var form = document.forms.namedItem("fileinfo");
    // form.addEventListener('submit', function(ev) {
    //
    //     var oOutput = document.querySelector("div"),
    //         oData = new FormData(form);
    //
    //     oData.append("CustomField", "This is some extra data");
    //     console.log(oData);
    //
    //     // var oReq = new XMLHttpRequest();
    //     // oReq.open("POST", "http://localhost/Exercise/index2.php", true);
    //     // oReq.onload = function(oEvent) {
    //     //     if (oReq.status == 200) {
    //     //         oOutput.innerHTML = "Uploaded!";
    //     //     } else {
    //     //         oOutput.innerHTML = "Error " + oReq.status + " occurred when trying to upload your file.<br \/>";
    //     //     }
    //     // };
    //     //
    //     // oReq.send(oData);
    //     var url="http://localhost/Exercise/index2"
    //     $.ajax({
    //         url:url,
    //         type:'POST',
    //         data:oData,
    //         dataType:'jsonp',
    //         crossDomain:true,
    //         processData:false,
    //         contentType:false
    //         // success:function (result_data) {
    //         //     console.log(result_data);
    //         // }
    //     }).done(function(res){
    //         console.log('done');
    //     }).fail(function(res){
    //         console.log('fail');
    //     }).success(function (res) {
    //         console.log('success');
    //     });
    //     ev.preventDefault();
    // }, false);

    $(document).ready(function()
    {
        $('#btn').click(function(e){
            var formData = new FormData();
            formData.append('file', $('#file')[0].files[0]);
            $.ajax({
                url:'http://127.0.0.1:17777/search/' + $('#select')[0].value,
                type:"POST",
                processData:false,
                contentType: false,
                data:formData,
                dataType:'json',
                crossDomain:true,
                success:function(msg){
                    console.log(msg);
                },
                error:function(msg){
                    console.log('error');
                }
            });
            //     .done(function(msg){
            //     console.log(msg);
            // }).error(function () {
            //     console.log('fail');
            // }).always(function(){
            //     console.log('complete');
            // });
            // console.log(formData.get('file'));
        })
    })
}