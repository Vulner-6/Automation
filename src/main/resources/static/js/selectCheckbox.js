//利用jquery的语法，让文档加载完毕后，再执行这里的代码，否则会获取不到
$(function ()
{
    var $inputs=$("input[name='poc']");
    var $isSelect=$("input[name='isSelect']");
    $isSelect.click(function ()
    {
        if(this.checked)
        {
            $inputs.each(function ()
            {
                $(this).prop('checked',true);
            })
        }
        else
        {
            $inputs.each(function ()
            {
                $(this).prop('checked',false)
            })
        }
    })
});