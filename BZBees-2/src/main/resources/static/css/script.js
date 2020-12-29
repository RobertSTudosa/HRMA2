 const userPlus = document.getElementsByClassName('button-userplus')[0];
// const userNormal = document.getElementsByClassName('button-user')[0];
 
 const navbarLinks = document.getElementsByClassName('navbar-links')[0];
 const navbarUserLinks = document.getElementsByClassName('navbar-user-links')[0];


 userPlus.addEventListener('click', () => {	 
     navbarLinks.classList.toggle('active');
     navbarUserLinks.classList.toggle('active');
 });
 
 
/* //try to close when clicked outside the menu
 document.addEventListener('click', (event) => {
	 if(!userPlus.contains(event.target)); 
		 navbarLinks.classList.toggle('');
		 navbarUserLinks.classList.toggle('');
 })*/
 
 






// const userPlus = document.getElementsByClassName('button-userplus')[0]
// const user = document.getElementsByClassName('button-user')[1]
// 
// const navbarLinks = document.getElementsByClassName('navbar-links')[0]
// const navbarUserLinks = document.getElementsByClassName('navbar-user-links')[1]
//
// userPlus.addEventListener('click', () => {
//     navbarLinks.classList.toggle('active')
// })
// 
//  user.addEventListener('click', () => {
//     navbarUserLinks.classList.toggle('active')
// })
// 
 
 

 //var userPlus = document.getElementsByClassName('button-userplus')[0];
 //var navbarLinks = document.getElementsByClassName('navbar-links')[0];
 //userPlus.addEventListener('click', function() {
 //    navbarLinks.classList.toggle('active');
 //});