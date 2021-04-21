 const userPlus = document.getElementsByClassName('button-userplus')[0];

 
 const navbarLinks = document.getElementsByClassName('navbar-links')[0];
 const navbarUserLinks = document.getElementsByClassName('navbar-user-links')[0];


 userPlus.addEventListener('click', () => {	 
     navbarLinks.classList.toggle('active');
     navbarUserLinks.classList.toggle('active');
 });

const notifButton = document.getElementsByClassName('notif-button')[0];

const notifLinks = document.getElementsByClassName('notifications-links')[0];


if(typeof(notifButton) != undefined && notifButton != null) {
notifButton.addEventListener('click', () => {
	notifLinks.classList.toggle('active');
});

}


//click out close for all interactive elements
$(document).mouseup (e => {
	if(!$('.navbar-user-links').is(e.target) &&
		$('.navbar-user-links').has(e.target).length === 0) {
		$('.navbar-user-links').removeClass('active');
	}
	
	if(!$('.navbar-links').is(e.target) &&
		$('.navbar-links').has(e.target).length === 0) {
		$('.navbar-links').removeClass('active');
	}
	
	if(!$('.notifications-links').is(e.target) &&
		$('.notifications-links').has(e.target).length === 0) {
		$('.notifications-links').removeClass('active');
	}
	
	if(!$('.buttonShare').is(e.target) &&
		$('.buttonShare').has(e.target).length === 0) {
		$('.buttonShare').removeAttr('open');
	}
	
});


//enable bs popover everywhere... yey ... free popovers everywheereee!!!
var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
  return new bootstrap.Popover(popoverTriggerEl)
})

/* code for 

var progressBar = {
	value: '',
	
	progressBarCount() {
		
		console.log('Progress count is ${this.ProgressCount}')
		},
		
		get progressCount () {
			this.value;
		},
		
		set progressCount (value) {
			this.value = value;
			this.progressBarCount();
		}
	
}
*/








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
 
 

