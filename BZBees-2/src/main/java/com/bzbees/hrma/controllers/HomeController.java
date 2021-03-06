package com.bzbees.hrma.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.CompanyDoc;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Like;
import com.bzbees.hrma.entities.Notification;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.SocialMedia;
import com.bzbees.hrma.entities.Tag;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.AsyncService;
import com.bzbees.hrma.services.CompanyDocService;
import com.bzbees.hrma.services.JobService;
import com.bzbees.hrma.services.LikeService;
import com.bzbees.hrma.services.NotificationService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.SocialMediaService;
import com.bzbees.hrma.services.TagService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/")
@SessionAttributes({"agency", "agenciesList","person","userAccount","lastAgencyPicList",
			"agencyJobList","jobTagsList", "jobList","userNotifs","jobTagsList","userJobsInList","userJobsIdApplied"})
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
	
	@Autowired
	LikeService likeServ;
	
	@Autowired
	private AsyncService asyncServ;
	
//	@Value("${version}")
//	private String ver;
	
	@GetMapping("/")
	public String displayHome ( Model model, @ModelAttribute("message") String message, 
			Authentication auth, HttpSession session ) {

		
		if(auth !=null ) {
			String name = auth.getName();
			
			User user = (User) userServ.loadUserByUsername(name);
			
			if(user == null) {
				auth.setAuthenticated(false);
				return "/";
			}
			session.setAttribute("userAccount", user);
			
			
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
				Set<Long> userAgencyIdLiked = likeServ.findLikedAgencyIdsByUsername(auth.getName());
				model.addAttribute("userAgencyLiked",userAgencyIdLiked);
				
			} else {
				System.out.println("Empty agency list");
				model.addAttribute("userAgencyLiked", new HashSet<Long>());
				model.addAttribute("agenciesList", new ArrayList<Agency>());
			}
			
			//get all the jobs in a list when authenticated()
			if(!jobServ.getAll().isEmpty()) {
				List<Job> allJobs = jobServ.findJobsPostedByAgencies();
				model.addAttribute("jobList", allJobs);
				Set<Long> userJobsIdLiked = likeServ.findLikedJobsIdsByUsername(auth.getName());				
				model.addAttribute("userJobsLiked", userJobsIdLiked);				
				Set<Long> userJobsInList = jobServ.findJobsIdAddedToListByPersonId(user.getUserId());
				model.addAttribute("userJobsInList", userJobsInList);
				Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());				
				model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
		
			} else {
				model.addAttribute("userJobsIdApplied", new HashSet<Long>());
				model.addAttribute("jobList", new ArrayList<Job>());
				model.addAttribute("userJobsLiked", new HashSet<Long>());
				model.addAttribute("userJobsInList", new HashSet<Long>());
			}
			
			//get the logged in user notifs
			if(!notifServ.findNotificationsByUserId(user.getUserId()).isEmpty()) {
				List<Notification> allUserNotif = notifServ.reverseFindNotificationsByUserId(user.getUserId());
				List<Notification> showUserNotifs = new ArrayList<>();
				int count = allUserNotif.size();
				for(int i = 0; i < 4; i++) {
				
					if(count == 0) {
						break;
					}
					showUserNotifs.add(allUserNotif.get(i));
					count = count -1;
					System.out.println(allUserNotif.get(i).getNotificationId() 
							+ " " + allUserNotif.get(i).getDateCreated());
				}

				model.addAttribute("userNotifs", showUserNotifs);
			} else {
				model.addAttribute("userNotifs", new ArrayList<>());
			}
			
			
		
		}
		
		//NULL AUTH STARTS HERE ---
		
		
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
			List<Tag> agencyJobsTags = new ArrayList<>();
			for(Job job : allJobs) {
				List<Tag> jobsTags = tagServ.find2TagsByJobId(job.getJobId());
				agencyJobsTags.addAll(jobsTags);
			}
			
			model.addAttribute("jobTagsList", agencyJobsTags);
			System.out.println("There are jobs in the list");
			
		} else {
			model.addAttribute("jobList", new ArrayList<Job>());
			model.addAttribute("jobTagsList", new ArrayList<Tag>());
		}
		

		
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
		
		if(auth == null) {
			return "user/login";
		}
		
		if(auth != null) {
			user = (User) userServ.loadUserByUsername(auth.getName());
			
			if(user == null) {
				auth.setAuthenticated(false);
				return "/";
			}

			// get the person from repo user query
			Person person = persServ.findPersonByUserId(user.getUserId());
			model.addAttribute("person", person);
			model.addAttribute("userAccount", user);
			
			Set<Long> userJobsIdInList = jobServ.findJobsIdAddedToListByPersonId(person.getPersonId());
			model.addAttribute("userJobsInList", userJobsIdInList);
			
			Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());
			model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
			
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
					Set<Person> allApplicantsApproved = new HashSet<Person>();
					Set<Long> allCandidatesIdsWithValidDate = new HashSet<Long>();
					for(Job job : agencyJobs) {
						List<Tag> jobsTags = tagServ.findTagsByJobId(job.getJobId());
						agencyJobsTags.addAll(jobsTags);
						Set<Person> applicantsApproved = persServ.getCandidatesApprovedToJob(job.getJobId());
						allApplicantsApproved.addAll(applicantsApproved);
						
						
						
						Set<Long> applicantsWithValidDate = persServ.getCandidatesIdsWithValidDatesByJobId(job.getJobId());

						allCandidatesIdsWithValidDate.addAll(applicantsWithValidDate);
					}
					if(auth != null) {
						Set<Long> userJobsIdLiked = likeServ.findLikedJobsIdsByUsername(auth.getName());			
						Set<Long> userJobsIdInList = jobServ.findJobsIdAddedToListByPersonId(user.getUserId());
						Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());
						model.addAttribute("userJobsLiked", userJobsIdLiked);
						model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
						model.addAttribute("userJobsInList", userJobsIdInList);
						
						model.addAttribute("jobTagsList", agencyJobsTags);
						
						
					} else {
						model.addAttribute("userJobsLiked", new HashSet<String>());
					}

					model.addAttribute("jobTagsList", agencyJobsTags);
					model.addAttribute("allApplicantsApproved", allApplicantsApproved);
					if(allCandidatesIdsWithValidDate != null  ) {
						model.addAttribute("allCandidatesIdsWithValidDate", allCandidatesIdsWithValidDate);
					} 
					
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
								model.addAttribute("match", false);
							}
						}
					
					} else {						
						model.addAttribute("match", false);
						model.addAttribute("agencyMessage", "You can affiliate with this agency by clicking \'get affiliated\' button. ");
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
							List<Notification> showUserNotifs = new ArrayList<>();
							int count = allUserNotif.size();
							for(int i = 0; i < 4; i++) {
							
								if(count == 0) {
									break;
								}
								showUserNotifs.add(allUserNotif.get(i));
								count = count -1;
								
							}

							model.addAttribute("userNotifs", showUserNotifs);
							
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
	
	
	@GetMapping(value="/getAllNotifications")
	@ResponseStatus(value = HttpStatus.OK)
	public  String  getAllUserNotifs (@RequestParam ("notifId") long notifId, Model model, Authentication auth,
			RedirectAttributes redirAttr) {
		
		List<Notification> showUserNotifs = new ArrayList<>();
		
		if(auth != null) {
			
			System.out.println("notification id is -----> " + notifId);
			
			User loggedInUser = (User) userServ.loadUserByUsername(auth.getName());
			
			
			
			List<Notification> reverseUserNotifs = notifServ.reverseFindNotificationsByUserId(loggedInUser.getUserId());
						
			
			
			int count = 0;
			
			for(int i = 0; i < reverseUserNotifs.size(); i++) {
				
				
				
				if(count < 4)
					
				{
					//while count is not smaller than 4 
					showUserNotifs.add(reverseUserNotifs.get(i)); //get 221 
					System.out.println("Notification id added ===> " + reverseUserNotifs.get(i).getNotificationId());
			
					//get the biggest notifId first 
					//add it to the list 
					//check if it is smaller than the notifId I have in the the method
					//if it is smaller add it and count++
					if(reverseUserNotifs.get(i).getNotificationId() < notifId) {
						count++;
						
						System.out.println("count is -----> " + count);
					}
					
				}
							
			}

			for(Notification notif : showUserNotifs) {
				System.out.println("Notification id is ===? " + notif.getNotificationId());
			}
			
//			redirAttr.addFlashAttribute("userNotifs", showUserNotifs);
			model.addAttribute("userNotifs", showUserNotifs);

			

		}
		
		return "header :: #updatedNotifs";
		
	
	}
	
	
	@GetMapping("/deleteNotif")
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteNotifs(@RequestParam ("notifId") long notifId, Authentication auth, Model model) {
		
		Notification notif = notifServ.findNotifByNotifId(notifId);
		notifServ.deleteNotificationById(notif);
		User loggedInUser = (User) userServ.loadUserByUsername(auth.getName());

		List<Notification> reverseUserNotifs = notifServ.reverseFindNotificationsByUserId(loggedInUser.getUserId());
		
		model.addAttribute("userNotifs", reverseUserNotifs);
		
	}
	
	@GetMapping("/likeJob")
	@ResponseStatus(value = HttpStatus.OK)
	public void likedJob(@RequestParam ("jobId") long jobId, Authentication auth, Model model) {
		if(auth != null) {
			Job theJob = (Job) jobServ.findJobById(jobId);
			
			if(likeServ.findLikeByJobIdAndUsername(jobId, auth.getName()) == null) {
				Like newLike = new Like(new Date(),theJob,null,auth.getName());
				likeServ.saveLike(newLike);
				theJob.setJobLikesCount(theJob.getJobLikesCount() + 1);
				jobServ.save(theJob);
												
			} else {
				Like unLike = likeServ.findLikeByJobIdAndUsername(jobId, auth.getName());
				likeServ.deleteLike(unLike);
				theJob.setJobLikesCount(theJob.getJobLikesCount() - 1);
				jobServ.save(theJob);
				
			}
		} 
	}
	
	
	@GetMapping("/likeAgency")
	@ResponseStatus(value = HttpStatus.OK)
	public void likedAgency(@RequestParam ("agencyId") long agencyId, Authentication auth, Model model) {
		if(auth != null) {
			
			Agency theAgency = (Agency) agencyServ.findAgencyByID(agencyId);
			
			if(likeServ.findLikeByAgencyIdAndUsername(agencyId, auth.getName()) == null) {
				Like newLike = new Like(new Date(),null, theAgency,auth.getName());
				likeServ.saveLike(newLike);
				theAgency.setAgencyLikesCount(theAgency.getAgencyLikesCount() + 1);
				agencyServ.saveAgency(theAgency);
			} else {
				Like unLike = likeServ.findLikeByAgencyIdAndUsername(agencyId, auth.getName());
				likeServ.deleteLike(unLike);
				theAgency.setAgencyLikesCount(theAgency.getAgencyLikesCount() -1);
				agencyServ.saveAgency(theAgency);
			}
			
		}
	}
	
	
	@GetMapping("/showJob/{jobId}")
	public String showJob(@PathVariable("jobId") long jobId, Authentication auth, Model model) {
		
		if(!model.containsAttribute("job")) {
			Job theJob = (Job) jobServ.findJobById(jobId);
			model.addAttribute("job", theJob);
			Agency aAgency = (Agency) theJob.getTheAgency();
			System.out.println("agency if model contains job is ------> " + aAgency.getAgencyName());
			model.addAttribute("aAgency", aAgency);
			
			
			if(auth != null) {
				User user = (User) userServ.loadUserByUsername(auth.getName());
				Set<Long> userJobsIdLiked = likeServ.findLikedJobsIdsByUsername(auth.getName());				
				model.addAttribute("userJobsLiked", userJobsIdLiked);
				
				
				
				Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());
				model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
				
				Set<Long> userJobsInList = jobServ.findJobsIdAddedToListByPersonId(user.getUserId());
				model.addAttribute("userJobsInList", userJobsInList);
				
				System.out.println("agency if model contains job is and user is authenticated ------> " + aAgency.getAgencyName());
				model.addAttribute("aAgency", aAgency);
				
				Set<Long> userAgencyIdLiked = likeServ.findLikedAgencyIdsByUsername(auth.getName());
				model.addAttribute("userAgencyLiked",userAgencyIdLiked);

				
			} 
			
		} else {
			
			model.addAttribute("job", new Job());
			model.addAttribute("userJobsLiked", new HashSet<>());
			model.addAttribute("aAgency", null);
			System.out.println("agency if model DOES NOT contains job is ------> NULL" );
			model.addAttribute("userAgencyLiked", new HashSet<Long>());
		}
			
		return "agency/job";
	}
	
	@GetMapping("/addJobToList")
	@ResponseStatus(value = HttpStatus.OK)
	public String addJobToUserList(@RequestParam("jobId") long jobId, Model model, Authentication auth) {
		
		if(auth == null) {
			return "user/login";
		}
		
		//get the job by id
		Job theJob = jobServ.findJobById(jobId);
		// check if there is authentication 
		if(auth != null) {
		//retrieve the user
		User loggedInUser = (User) userServ.loadUserByUsername(auth.getName());

		//retrieve the person
		Person person = (Person) persServ.findPersonByUserId(loggedInUser.getUserId());
		
		//initiate a set of jobs
		Set<Job> personList = new HashSet<>();
		
		
		//check if the set of jobs is not empty
		
		if(jobServ.findJobsAddedToListByPersonId(person.getPersonId()) != null) {
		
			//query the list of jobs from the person and attribute it to the Set
			personList = jobServ.findJobsAddedToListByPersonId(person.getPersonId());
			if(personList.contains(theJob)) {
				personList.remove(theJob);
				person.setJobsInList(personList);
				persServ.save(person);
			} else {
				//add the job in the param
				personList.add(theJob);
				//set this Set to the person in auth
				person.setJobsInList(personList);
				//save the person 
				persServ.save(person);
				
			}
						
		} else {
			//add the job in the param
			personList.add(theJob);
			//set this Set to the person in auth
			person.setJobsInList(personList);
			//save the person 
			persServ.save(person);
			
		}
				
		}
		
		return null;
		
	}
	
	
	@GetMapping("/applyToJob")
	@ResponseStatus(value = HttpStatus.OK)
	public String applyToJob(@RequestParam("jobId") long jobId, Model model, Authentication auth, HttpServletResponse httpServletResponse) throws IOException {
		
		if(auth == null) {
			return "user/login";
		}

		//get the job from the model 
		Job theJob = (Job) jobServ.findJobById(jobId);
		
		//get the agency that posted the job
		Agency theAgency = (Agency) agencyServ.findAgencyByJobId(jobId);
		
		//get the admin of the agency
		String agencyAdminName = theAgency.getAdminName();
		User agencyAdmin = (User) userServ.loadUserByUsername(agencyAdminName);
		//get the person associated with the agency admin
		Person agencyAdminPerson = (Person) persServ.findPersonByUserId(agencyAdmin.getUserId());
		
		//check if there is an authentication 
		if(auth != null) {
			//retrieve the user logged in
			User loggedInUser = (User) userServ.loadUserByUsername(auth.getName());

			//retrieve the user details
			Person person = (Person) persServ.findPersonByUserId(loggedInUser.getUserId());
			
			//check if the person is affiliated to the agency 
			List<User> affiliatedUsers = userServ.getAllAffiliatedUsersByAgencyId(theAgency.getAgencyId());
			//verify is the person logged in is affiliated with the agency
				if(affiliatedUsers.contains(loggedInUser)) {
					
					//add the job to the list of applied jobs
					Set<Job> appliedJobs = person.getJobsApplied();
					appliedJobs.add(theJob);
					person.setJobsApplied(appliedJobs);
					
					//call async for notification
					long candidateId = person.getPersonId();
					long agencyId = theAgency.getAgencyId();
					
					asyncServ.createNotificationForJobAppliedByUser(candidateId, jobId, agencyId);
					
					//add the job to the list of jobs of the candidate
					person.addJobsApplied(theJob);
					persServ.save(person);
				
					
					
				} else {
					model.addAttribute("message", "not affiliated to this agency");
					
					return "redirect:/agency/profile";
				}

			
		}
		
		
		return null;
		
	}
	

}



















