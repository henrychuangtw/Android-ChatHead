FB-ChatHead
===========

ChatHead like FB messenger

<br/><br/>

How to Use
-----------
Start ChatHead :
```Java
startService(new Intent(Main.this, ChatHeadService.class));
```
<br/>
Show Message(see below pic 'Messager') :
```Java
Intent it = new Intent(Main.this, ChatHeadService.class);
it.putExtra(Utility.EXTRA_MSG, str);
startService(it);
```
<br/>
Stop ChatHead(see below pic 'LongPressing to Stop') :<br/>
long click and move chathead to the round on bottom


ScreenShot
-----------
**Dragging and Bounce animation**<br/>
![](assets/drag.png)

<br/><br/>
**Messenger**<br/>
![](assets/messenger1.png)
<br/>
![](assets/messenger2.png)

<br/><br/>
**LongPressing to Stop**<br/>
![](assets/pic_delete.png)


<br/><br/>
henrychuang.tw@gmail.com
