package com.bzbees.hrma.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.CompanyDoc;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Notification;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.SocialMedia;
import com.bzbees.hrma.entities.Tag;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.CompanyDocService;
import com.bzbees.hrma.services.JobService;
import com.bzbees.hrma.services.NotificationService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.SocialMediaService;
import com.bzbees.hrma.services.TagService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/")
@SessionAttributes({"agency", "agenciesList","person","userAccount","lastAgencyPicList",
			"agencyJobList","jobTagsList", "jobList","userNotifs"})
public class HomeController {
	
	@Autowired
	UserService userServ;
	
	@Autowired
	PersonService persServ;
	
	@Autowired
	AgencyService agencyServ;
	
	@Autowired
	ProfileImgService profileImgServ;
	
	@Autowired
	private CompanyDocService companyDocServ;
	
	@Autowired
	private SocialMediaService socialMediaServ;
	
	@Autowired
	BCryptPasswordEncoder bCryptEncoder;
	
	@Autowired
	JobService jobServ;
	
	@Autowired
	TagService tagServ;
	
	@Autowired
	NotificationService notifServ;
	
//	@Value("${version}")
//	private String ver;
	
	@GetMapping("/")
	public String displayHome ( Model model, @ModelAttribute("message") String message, 
			Authentication auth, HttpSession session ) {
		
		if(auth !=null ) {
			String name = auth.getName();
			
			User user = (User) userServ.loadUserByUsername(name);
			session.setAttribute("userAccount", user);
			
			if(user == null) {
				return "redirect:/\\logout";
			}
			
			
			//get authorities to string 			
			System.out.println("User's role in my home controller are: " + user.getRoles().toString());
			
			model.addAttribute("person", persServ.findPersonByUserId(user.getUserId()));
			model.addAttribute("userAccount", user);
			
				
			
			//get the agency if any is associated with current user 
			if(agencyServ.findAgencyByUserId(user.getUserId()) !=null) {
				Agency agency = agencyServ.findAgencyByUserId(user.getUserId());
				System.out.println("Agency in home controller " + agency.getAgencyName());
				model.addAttribute("agency", agency);
			} else {
				Agency agency = new Agency();
				model.addAttribute("agency", agency);
				System.out.println("THERE IS NO AGENCY");
			}
			
			//get all the agencies in a list 
			if(!agencyServ.findAll().isEmpty()) {
				List<Agency> allAgencies = agencyServ.findAll();
				System.out.println("adding agencies ---> " + allAgencies.size());
				model.addAttribute("agenciesList", allAgencies);
				
			} else {
				System.out.println("Empty agency list");
				model.addAttribute("agenciesList", new ArrayList<Agency>());
			}
			
			//get all the jobs in a list when authenticated()
			if(!jobServ.getAll().isEmpty()) {
				List<Job> allJobs = jobServ.findJobsPostedByAgencies();
				model.addAttribute("jobList", allJobs);
				
			} else {
				model.addAttribute("jobList", new ArrayList<Job>());
			}
			
			//get the logged in user notifs
			if(!notifServ.findNotificationsByUserId(user.getUserId()).isEmpty()) {
				List<Notification> allUserNotif = notifServ.reverseFindNotificationsByUserId(user.getUserId());
				model.addAttribute("userNotifs", allUserNotif);
			} else {
				model.addAttribute("userNotifs", new ArrayList<>());
			}
		
		}
		
		
		//get all the agencies in a list 
		if(!agencyServ.findAll().isEmpty()) {
			List<Agency> allAgencies = agencyServ.findAll();
			System.out.println("adding agencies ---> " + allAgencies.size());
			model.addAttribute("agenciesList", allAgencies);
 			
		} else {
			System.out.println("Empty agency list");
			model.addAttribute("agenciesList", new ArrayList<Agency>());
		}
		
		
		// get all the jobs in a list when anonymous() or not - 
		// get all the jobs that are posted by agencies !!!! 
		// otherwise calling lastAgencyImageId on null will not work
		if(!jobServ.getAll().isEmpty()) {
			List<Job> allJobs = jobServ.findJobsPostedByAgencies();
			model.addAttribute("jobList", allJobs);
			System.out.println("There are jobs in the list");
			
		} else {
			model.addAttribute("jobList", new ArrayList<Job>());
		}
		
	
//		System.out.println("Name of the user : " + auth.getName());
//		model.addAttribute("localVerNumber", ver);
		
		return "home";
	}
	
	@GetMapping("/showChangePassword")
	public String changePassword (Model model, Authentication auth) {
		
		if(auth.getName()!=null) {
			System.out.println("is there any user? : " + auth.getName());
		}


		
		return "user/changePass";
	}
	
	@PostMapping("/changePassword")
	public String changePassword(Model model, Authentication auth, HttpServletRequest request,
					RedirectAttributes redirAttr) {
		
		User user = (User) userServ.loadUserByUsername(auth.getName());
		user.setPassword(bCryptEncoder.encode(request.getParameter("newPassword")));
		
		Person person = persServ.findPersonByUserId(user.getUserId());
		persServ.save(person);
		userServ.save(user);
		
		redirAttr.addAttribute("passChanged", "Password successfully updated.");
		redirAttr.addAttribute("success", "Changed password for user: " + user.getUsername());
		
		return "redirect:/person/sprofile";
		
	}
	

	@GetMapping(value="/agencyProfile")
	public String showAgencyProfile(@RequestParam("id") long id, Model model, Authentication auth) {
		
		User user = null;
		
		if(auth != null) {
			user = (User) userServ.loadUserByUsername(auth.getName());

			// get the person from repo user query
			Person person = persServ.findPersonByUserId(user.getUserId());
			model.addAttribute("person", person);
			model.addAttribute("userAccount", user);
			
		}	
		
		
		System.out.println("Agency retrieved on a home page without login ");
		System.out.println("agency id from the page ---> " + id);
		Agency agency = agencyServ.findAgencyByID(id);
		System.out.println("Agency retrieved is --->  " + agency.getAgencyName());
		model.addAttribute("agency", agency);
		
		// get the agency pics from the profileImg repo
				List<ProfileImg> agencyPics = profileImgServ.getPicsByAgencyId(agency.getAgencyId());

				// get the last pic of the agency's profile from profileImg repo
				ProfileImg theImg = profileImgServ.getLastAgencyPic(agency.getAgencyId());
				if (theImg != null) {
					model.addAttribute("img", theImg);
					System.out.println("In the !if null image of the agency w/o login" + 
						" id of the pic ===>" +	theImg.getPicId());

					List<ProfileImg> lastPicList = new ArrayList<>();
					lastPicList.add(profileImgServ.getLastAgencyPic(agency.getAgencyId()));
					model.addAttribute("lastAgencyPicList", lastPicList);

				} else {
					agencyPics.clear();
					System.out.println("ALL CLEAR");
					model.addAttribute("img", new ProfileImg());
					model.addAttribute("lastAgencyPicList", new ArrayList<>());
				}

				if (companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()) == null) {
					model.addAttribute("companyDocList", new ArrayList<>());
					System.out.println("list is empty");
				} else {
					List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());
					model.addAttribute("companyDocList", docList);

					// insert below check validation code for different documents uploaded
				}
		
				if (socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).isEmpty()) {
					System.out.println("Social Media List is empty");
					model.addAttribute("socialMediaList", new ArrayList<>());
					
				} else {
					List<SocialMedia> socList = socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId());
					for(SocialMedia link : socList) {
						System.out.println("name of the link --->" + link.getName());
						if(link.getName().contains("FACEBook")) {
							System.out.println("Inside facebook");
							String FBIcon = "<span style=\"color: #E9D415;background-color: #484848\" class=\"iconify\" data-inline=\"false\" data-icon=\"dashicons:facebook\"></span>";
							model.addAttribute("spanFB", FBIcon);
						}
						
						if(link.getName().contains("LINKedin")) {
							System.out.println("Finding nemo on linkedin");
							String LNIcon = "<span style=\"color: #E9D415;background-color: #484848;\" class=\"iconify\" data-inline=\"false\" data-icon=\"el:linkedin\"></span>";
							model.addAttribute("spanLN", LNIcon);
						}
						
						if(link.getName().contains("TWITter")) {
							System.out.println("Fly little bird, fly");
							String TWIcon = "<span style=\"color:#484848;background-color: #E9D415;\" class=\"iconify\" data-inline=\"false\" data-icon=\"vaadin:twitter-square\"></span>";
							model.addAttribute("spanTW", TWIcon);
						}
						
						if(link.getName().contains("INSTAgram")) {
							System.out.println("It's all for the gram");
							String INIcon = "<span style=\"color: #E9D415;background-color: #484848;\" class=\"iconify\" data-icon=\"entypo-social:instagram\" data-inline=\"false\"></span>";
							model.addAttribute("spanIN", INIcon);
						}
			
					}
					
					model.addAttribute("socialMediaList", socList);
				}
				
				
				if(userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId()) != null) {
					List<User> affiliatedUsersList = userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId());
					List<Person> personsAffiliated = new ArrayList<>();
					Person affiliatedPerson = new Person ();
					for (User affUser : affiliatedUsersList) {
						affiliatedPerson = persServ.findPersonByUserId(affUser.getUserId());
						personsAffiliated.add(affiliatedPerson);
						if(profileImgServ.getLastProfilePic(affiliatedPerson.getPersonId()) != null) {
							ProfileImg lastImg = profileImgServ.getLastProfilePic(affiliatedPerson.getPersonId());
							affiliatedPerson.setLastImgId(lastImg.getPicId());
						} else {
							System.out.println("Id ul ultimei imagini este null");;
						}
					}
					model.addAttribute("affiliatedPersonsList", personsAffiliated);
					
					System.out.println("Any affiliated users around here?");
				} else {
					System.out.println("No no users here");
					model.addAttribute("affiliatedPersonsList", new ArrayList<Person>());
				}
				
				
				if(jobServ.findJobsByAgencyId(agency.getAgencyId()) !=null) {
					List<Job> agencyJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
					model.addAttribute("agencyJobList", agencyJobs);
					List<Tag> agencyJobsTags = new ArrayList<>();
					for(Job job : agencyJobs) {
						List<Tag> jobsTags = tagServ.findTagsByJobId(job.getJobId());
						agencyJobsTags.addAll(jobsTags);
					}
					
					model.addAttribute("jobTagsList", agencyJobsTags);
					
				} else {
					model.addAttribute("agencyJobList", new ArrayList<>());
					model.addAttribute("jobTagsList", new ArrayList<>());
				}
				
				if(!model.containsAttribute("job")) {
					model.addAttribute("job", new Job());
				}
				
				
				// get all the names of the users that hit 'get affiliated' on this agency
				if(auth !=null) {
					
					//implement a db query to get all pending users for the agency via agency id
					if(!userServ.getAllPendingUsersByAgencyId(agency.getAgencyId()).isEmpty()  ) {				
						
					List<User> pendingUsers = userServ.getAllPendingUsersByAgencyId(id);
						for(User pendingUser : pendingUsers) {
							if(pendingUser.getUserId() == user.getUserId()) {
								System.out.println("match is true");
								model.addAttribute("match", true);
								model.addAttribute("agencyMessage", "You're affiliation with this agency is in pending. Please wait until the agency will approve your request.");
							} else {
								System.out.println("match is false from within pending users");
								model.addAttribute("match", false);
							}
						}
					
					} else {
						System.out.println("match is false because the pending user list is empty");
						model.addAttribute("match", false);
					}
				
				}
				
				
					if(auth !=null) {
					
					//implement a db query to get all pending users for the agency via agency id
					if(!userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId()).isEmpty()) {				
						
					List<User> affiliatedUsers = userServ.getAllAffiliatedUsersByAgencyId(id);
						for(User affiliatedUser : affiliatedUsers) {
							if(affiliatedUser.getUserId() == user.getUserId()) {
								System.out.println("affiliate is true");
								model.addAttribute("affiliate", true);
								model.addAttribute("agencyMessage", "You are affiliated with this agency. Now you can apply to this agency's jobs. \n "
										+ "If you no longer want to be affiliated please click the 'disaffiliate' button above.");
							} else {
								System.out.println("match is false from within affiliated users");
								model.addAttribute("affiliate", false);
							}
						}
					
					} else {
						System.out.println("match is false because the affiliated user list is empty");
						model.addAttribute("affiliate", false);
					}
				
				}
					
					if(auth != null) {
						if(!notifServ.findNotificationsByUserId(user.getUserId()).isEmpty()) {
							List<Notification> allUserNotif = notifServ.reverseFindNotificationsByUserId(user.getUserId());
							model.addAttribute("userNotifs", allUserNotif);
						} else {
							model.addAttribute("userNotifs", new ArrayList<>());
						}
						
					}
				

				
		model.addAttribute("progressCount", 100);			
		
		return "agency/agency_profile";
	}
	
	@GetMapping(value="/notifMarkRead")
	@ResponseStatus(value = HttpStatus.OK)
	public void markNotificationAsRead(@RequestParam("id") long notificationId, Authentication auth ) {
		if(auth == null) {
			
		}
		
		User user = (User) userServ.loadUserByUsername(auth.getName());
		System.out.println("User is here =====> " + user.getUsername());
		System.out.println("Notification id in the model is ============> " + notificationId);
		
		Notification hitNotif = notifServ.findNotifByNotifId(notificationId);
		hitNotif.setRead(true);
		notifServ.saveNotif(hitNotif);
		System.out.println("Notification saved is ===> " + hitNotif.getNotificationId());
		
		Person userPerson = persServ.findPersonByUserId(user.getUserId());
		
		//get all notifications of the user in REVERSE and if all are read then
		//save for the user PERSON set the unread_notif to false and save person and user. 
		

		List<Notification> userNotifs = userPerson.getNotifications();
		
		System.out.println("Notification list size ====> " + userNotifs.size());

		boolean unreadNotifs = true; 
		
		for(int i = 0 ; i < userNotifs.size(); i++) {
			System.out.println("IN for loop");
			if(!userNotifs.get(i).isRead()) {
				System.out.println("This notification ---> " + userNotifs.get(i).getNotificationId() + " is unread");
				unreadNotifs = true;
				break;		
				
			} else {
				unreadNotifs = false;
				System.out.println("This notification ---> " + userNotifs.get(i).getNotificationId() + "is read");
			}
			
			}
		
		if(!unreadNotifs) {
			System.out.println("All notifications for " + user.getUsername() + " are read");
			
			userPerson.setUnreadNotifs(false);
			persServ.save(userPerson);
			userServ.save(user);
			
		}
		

		
		
		
	}
	
	
	


	

}



















