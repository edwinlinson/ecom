package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


import com.example.demo.Model.*;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Security.UserDetailsImpl;
import com.example.demo.Service.CouponService;
import com.example.demo.Service.UserServiceImpl;
import com.example.demo.ServiceImpl.OfferService;
import com.example.demo.ServiceImpl.OrderService;
import com.example.demo.dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Service.CategoryService;
import com.example.demo.Service.ProductService;
import com.example.demo.dto.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {
//	public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";
	public static String uploadDir =  "/home/ubuntu/ecom/src/main/resources/static/productImages";

	@Autowired
	CategoryService categoryService;
	@Autowired
	OfferService offerService;

	
	@Autowired
	ProductService productService;

	@Autowired
	UserServiceImpl userService;
	@Autowired
	UserRepo repo;
	@Autowired
	CouponService couponService;


	@GetMapping("/admin")
	public String getAdmin() {
		return "adminHome";
	}


	@GetMapping("/admin/dashboard")
	public String getAdmindash(Model model) {
		Calendar calendar = Calendar.getInstance();
		Date fromDate = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		Date toDate = calendar.getTime();
		System.out.println(" "+fromDate+ " " +toDate );
		List<Order> sales = orderService.getSalesData(fromDate, toDate);
		List<Report> monthlySales = generateReport(sales);
		double totalAmount = calculateMonthlyTotalAmount(monthlySales);
		model.addAttribute("sales", monthlySales);
		model.addAttribute("totalAmount", totalAmount);
		System.out.println(" " +totalAmount);
		return "/admin/index";
	}

	private double calculateMonthlyTotalAmount(List<Report> monthlySales) {
		return monthlySales.stream().mapToDouble(Report::getTotal).sum();
	}

	@GetMapping("/admin/categories")
	public String getcat(Model model) {
		model.addAttribute("categories",categoryService.getAllCategory());
		return "categories";
	}

	@PostMapping("/admin/categoriespop/add")
	public String postCatPopAdd(@ModelAttribute("category") @Valid Category category,BindingResult result,Model model) {
		if (result.hasErrors()){
			return "categoriesAdd";
		}
		if (categoryService.isCategoryExists(category.getName())){
			result.rejectValue("name", "error.category", "Category name already exists");
			return "redirect:/admin/categories/add";
		}
		categoryService.addCategory(category);
		return "redirect:/admin/products/add";
	}
	//Products
	@GetMapping("/admin/products")
	public String getProducts(Model model) {
		model.addAttribute("products",productService.getAllProducts());
		return"products";
	}
	@GetMapping("/admin/categories/delete/{id}")
	public String deleteCat(@PathVariable int id) {
		categoryService.deleteCategoryById(id);
		return "redirect:/admin/categories";
	}
	@GetMapping("/admin/categories/update/{id}")
	public String updateCat(@PathVariable int id, Model model) {
		Optional<Category> category = categoryService.getCatById(id);
		if(category.isPresent()) {
			model.addAttribute("category",category.get());
			return "categoriesAdd";
		}
		else return "404";
	}
	@GetMapping("/admin/categories/add")
	public String getcatadd(Model model) {
		model.addAttribute("category", new Category());
		return "categoriesAdd";
	}
	@GetMapping("/admin/coupons/add")
	public String getCouponAdd(Model model){
		model.addAttribute("coupon", new Coupon());
		return "/admin/CouponAdd";
	}

	@PostMapping("/admin/categories/add")
	public String postCatAdd(@ModelAttribute("category")@Valid Category category, BindingResult result, Model model){
		if (result.hasErrors()){
			return "categoriesAdd";
		}
		if (categoryService.isCategoryExists(category.getName())){
			result.rejectValue("name", "error.category", "Category name already exists");
			return "categoriesAdd";
		}
		categoryService.addCategory(category);
		return "redirect:/admin/categories";
	}

@GetMapping("/admin/products/add")
public String productAddGet(Model model) {
	model.addAttribute("productDTO", new ProductDTO());
	model.addAttribute("categories", categoryService.getAllCategory());
	return"productAdd3";
}

@PostMapping("/admin/products/addProduct")
public String productAddPost(
		 @Valid @ModelAttribute("productDTO") ProductDTO productDTO,
		 BindingResult result,
		@RequestParam("files") MultipartFile[] files,
		@RequestParam("imageNames") Set<String> imgNamesSet,
		Model model,
		RedirectAttributes attributes) throws IOException {
		try{
			if (productService.isProductExists(productDTO.getName()))
			{
				System.out.println("Product exists with the same name");
				attributes.addFlashAttribute("error", "A product with this name already exists");
				return "redirect:/admin/products/add";
			}

			if (result.hasErrors()) {
				System.out.println("in if condition");

				List<FieldError> branderr = result.getFieldErrors().stream().filter(error -> error.getField().equals("brand"))
						.toList();
				List<FieldError> imageError = result.getFieldErrors().stream().filter(error -> error.getField().equals("brand"))
						.toList();
				model.addAttribute("error","Some error Occured");
				if (!branderr.isEmpty()){
					System.out.println("Brand error");
					attributes.addFlashAttribute("errorb", "Brand name should not be empty");
				}
				if (!imageError.isEmpty()){
					System.out.println("Image error");
					attributes.addFlashAttribute("errorc","Image uploaded has some file error");
				}
				return "redirect:/admin/products/add";
			}
			if (result.hasFieldErrors("brand")){
				System.out.println("Brand error");
				attributes.addFlashAttribute("errorb","Brand name should not be blank");
//		attributes.addFlashAttribute("productDTO",productDTO);
				return "redirect:/admin/products/add";
			}
//	if (result.hasFieldErrors("imageNames")){
//		System.out.println("Image error");
//		attributes.addFlashAttribute("errorc","Image should be in jpg, jpeg or png format.");
//		attributes.addFlashAttribute("productDTO",productDTO);
//		return "redirect:/admin/products/add";
//	}

			Product product = new Product();
			product.setId(productDTO.getId());
			product.setName(productDTO.getName());
			product.setCategory(categoryService.getCatById(productDTO.getCategoryId()).get());
			product.setPrice(productDTO.getPrice());
			product.setBrand(productDTO.getBrand());
			product.setDescription(productDTO.getDescription());
//	List<String> imgNames = new ArrayList<>(imgNamesSet);

			Set<String> imageUUIDs = new HashSet<>();
			Iterator<String> imageNameIterator = imgNamesSet.iterator();

			for (MultipartFile file : files) {
				String imgName = imageNameIterator.hasNext() ? imageNameIterator.next() : null;
				if (!file.isEmpty()) {
					String imageUUID = UUID.randomUUID().toString();
					Path filenamePath = Paths.get(uploadDir, imageUUID);
					Files.write(filenamePath, file.getBytes());
					imageUUIDs.add(imageUUID);
				} else if (imgName != null) {
					imageUUIDs.add(imgName);
				}
			}
			product.setImageNames(imageUUIDs);
			productService.addProduct(product);

		} catch (Exception e){
			e.printStackTrace();
		}

	return "redirect:/admin/products";
}

	@GetMapping("/admin/product/delete/{id}")
	public String deleteProduct(@PathVariable Long id) {
		productService.removeProductbyId(id);
		return "redirect:/admin/products";
	}
	
	@GetMapping("/admin/product/update/{id}")
	public String updateProductGet(@PathVariable Long id, Model model) {
		Product product = productService.getProductById(id).get();
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setCategoryId(product.getCategory().getId());
		productDTO.setPrice(product.getPrice());
		productDTO.setBrand(product.getBrand());
		productDTO.setDescription(product.getDescription());
//		productDTO.setImgName(product.getImageNames());
		productDTO.setQty(product.getQty());

		model.addAttribute("categories",categoryService.getAllCategory());
		model.addAttribute("productDTO",productDTO);

		return"productsAdd";
	}
	@GetMapping("/admin/users")
	public String getUsers(Model model){
		model.addAttribute("users",userService.getAllUsers());
		return "users";
	}
	@GetMapping("/admin/users/update/{id}")
	public String updateuserGET(@PathVariable("id") int id, Model model, Principal principal){
		UserDTO existing = userService.findById(id);
		model.addAttribute("userDTO",existing);
		return "updateUser";
	}
	@PostMapping("/admin/users/update/{id}")
	public String updateUserPost( @ModelAttribute("userDTO") UserDTO userDTO){
//		UserDTO existing = userService.findById(id);
//		userService.saveUser(existing);
//		User existing = repo.findUserById(id);
//		repo.save(existing);
		userService.updateUser(userDTO);
		return "redirect:/admin/users";
	}
	@GetMapping("/admin/users/add")
	public String adduserGET(Model model, Principal principal){
		String username=principal.getName();
		User admin=userService.findByEmail(username);
		model.addAttribute("admin",admin);
		model.addAttribute("userDTO",new UserDTO());
		return "addUser";
	}
	@PostMapping("/admin/users/add")
	public String adduserPOST(@ModelAttribute("userDTO") UserDTO userDTO, Model model, BindingResult result){
		User existing = userService.findByEmail(userDTO.getEmail());
		if (existing !=null){
			result.rejectValue("email", "", "There is already an account registered with that email");
		}
		if (result.hasErrors()) {
			model.addAttribute("userDTO", userDTO);
			return "addUser";
		}

		userService.saveUserAdmin(userDTO);
		model.addAttribute("admin",new User());
		model.addAttribute("userDTO",new UserDTO());

		return  "redirect:/admin/users";
	}


	@GetMapping("/admin/users/delete/{id}")
	public String deleteProduct(@PathVariable int id) {
		userService.deleteUserById(id);
		return "redirect:/admin/users";
	}

@GetMapping("/admin/users/disable/{id}")
public String disableProduct(@PathVariable int id) {
	userService.disableUserById(id);
	return "redirect:/admin/users";
}
	@Autowired
	OrderService orderService;

	@GetMapping("/admin/orders")
	public String adminOrders(Model model){
		List<Order> orderList = orderService.getAllOrders();
		model.addAttribute("orderList" , orderList);
		return "/admin/OrderManage";
	}
	@PostMapping("/admin/orders/update-status/{orderId}")
	public String updateOrderStatus(@PathVariable Long orderId, @RequestParam("orderStatus") String orderStatus) {
		orderService.updateOrderStatus(orderId,orderStatus);
		return "redirect:/admin/orders";
	}
	@PostMapping("/admin/orders/cancel/{orderId}")
	public String cancelOrder(@PathVariable Long orderId){
		orderService.updateOrderStatus(orderId,"CANCELLED");
		return "redirect:/admin/orders";
	}
	@GetMapping("/admin/sales")
	public String sales(Model model){
		List<Order> orders = orderService.getAllOrders();
		model.addAttribute("orders", orders);
		return "/admin/salesReport";
	}
	@PostMapping("/admin/salesReport/")
	public String saleReport(@RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
							 @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate,
							 @RequestParam("dates") String dates, Model model) {
		System.out.println("from date is " + fromDate + " to date is " + toDate);
		List<Order> sales = orderService.getSalesData(fromDate, toDate);
		List<Report> dailySales = generateReport(sales);

		int totalOrders = dailySales.stream().mapToInt(Report::getNum).sum();
		double totalAmount = dailySales.stream().mapToDouble(Report::getTotal).sum();

		model.addAttribute("sales", dailySales);
		model.addAttribute("totalOrders", totalOrders);
		model.addAttribute("totalAmount", totalAmount);

		return "/admin/salesReport";
	}

	private List<Report> generateReport(List<Order> sales) {

		Map<String, Report> dailySalesMap = new LinkedHashMap<>();

		for (Order order : sales) {
			String orderDate = formatDate(order.getOrderDate());

			double orderTotal = order.getTotalPrice();

			Report report = dailySalesMap.getOrDefault(orderDate, new Report());
			report.setDate(orderDate);
			report.setNum(report.getNum() + 1);
			report.setTotal(report.getTotal() + orderTotal);
			dailySalesMap.put(orderDate, report);
		}

		List<Report> dailySalesReport = new ArrayList<>(dailySalesMap.values());
		return dailySalesReport;
	}
	private String formatDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	@GetMapping("/admin/coupons/toggleActivation/{id}")
	public String toggleActivation(@PathVariable("id") int id) {
		Coupon coupon = couponService.getCouponById(id).get();
		if (coupon != null) {
			coupon.toggleActive();
			couponService.updateCoupon(coupon);
		}
		return "redirect:/admin/coupons";
	}
	@GetMapping("/admin/coupons")
	public String getCoupons(Model model){
		model.addAttribute("coupons",couponService.getAllCoupons());
		return "/admin/coupons";
	}

	@GetMapping("/admin/offers")
	public String getOffers(Model model){
		model.addAttribute("offers",offerService.getAllOffers());
		return "/admin/offers";
	}
	@GetMapping("/admin/offers/add")
	public String getOfferAdd(Model model){
		model.addAttribute("products", productService.getAllProducts());
		model.addAttribute("categories",categoryService.getAllCategory());
		model.addAttribute("offer",new Offers());
		return "/admin/OfferAdd";
	}
	@PostMapping("/admin/offers/add")
	public String postOfferAdd(@ModelAttribute("offer")@Valid Offers offer,Model model, RedirectAttributes attributes){
		try{
			offerService.add(offer);
			attributes.addFlashAttribute("text","Offer added Successfully");
		}catch (Exception e){
			e.printStackTrace();
			attributes.addFlashAttribute("text","Offer add failed, please try again");
		}
		return "redirect:/admin/offers";
	}
	@PostMapping("/admin/coupons/add")
	public String postCouponAdd(@ModelAttribute("coupon") Coupon coupon, Model model){
		couponService.addCoupon(coupon);
		return "redirect:/admin/coupons";
	}





//	@PostMapping("/admin/products/add")
//	public String productAddPost(
//			@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
//			@RequestParam("files") MultipartFile[] files,
//			@RequestParam("imgNames") List<String> imgNames,
//			BindingResult result, Model model,
//			RedirectAttributes attributes) throws IOException {
//
//		if (productService.isProductExists(productDTO.getName())) {
//			System.out.println("Product exists with the same name");
//			attributes.addFlashAttribute("error", "A product with this name already exists");
//			return "redirect:/admin/products/add";
//		}
//
//		if (result.hasErrors()) {
//			model.addAttribute("productDTO", productDTO);
//			attributes.addFlashAttribute("error", "Some validation error occurred");
//			return "redirect:/admin/products/add";
//		}
//
//		Product product = new Product();
//		product.setId(productDTO.getId());
//		product.setName(productDTO.getName());
//		product.setCategory(categoryService.getCatById(productDTO.getCategoryId()).get());
//		product.setPrice(productDTO.getPrice());
//		product.setBrand(productDTO.getBrand());
//		product.setDescription(productDTO.getDescription());
//
//		// Handle multiple image uploads
//		Set<String> imageUUIDs = new HashSet<>();
//
//		for (int i = 0; i < files.length; i++) {
//			MultipartFile file = files[i];
//			String imgName = imgNames[i];
//
//			if (!file.isEmpty()) {
//				String imageUUID = UUID.randomUUID().toString(); // Generate a unique image name
//				Path filenamePath = Paths.get(uploadDir, imageUUID);
//				Files.write(filenamePath, file.getBytes());
//				imageUUIDs.add(imageUUID);
//			} else {
//				imageUUIDs.add(imgName);
//			}
//		}
//
//		product.setImageNames(imageUUIDs);
//		productService.addProduct(product);
//		return "redirect:/admin/products";
//	}

//	@PostMapping("/admin/products/add")
//	public String productAddPost(
//			@Valid @ModelAttribute("productDTO") ProductDTO productDTO,
//			@RequestParam("files") MultipartFile[] files,
//			@RequestParam("imgNames") Set<String> imgNamesSet,
//			BindingResult result,
//			Model model,
//			RedirectAttributes attributes) throws IOException {
//
//		Product product = new Product();
//		product.setId(productDTO.getId());
//		product.setName(productDTO.getName());
//		product.setCategory(categoryService.getCatById(productDTO.getCategoryId()).get());
//		product.setPrice(productDTO.getPrice());
//		product.setBrand(productDTO.getBrand());
//		product.setDescription(productDTO.getDescription());
//
////		List<String> imgNames = new ArrayList<>(imgNamesSet);
////		// Handle multiple image uploads
////		Set<String> imageUUIDs = new HashSet<>();
//
////		for (int i = 0; i < files.length; i++) {
////			MultipartFile file = files[i];
////			String imgName = imgNames[i];
////
////			if (!file.isEmpty()) {
////				String imageUUID = UUID.randomUUID().toString(); // Generate a unique image name
////				Path filenamePath = Paths.get(uploadDir, imageUUID);
////				Files.write(filenamePath, file.getBytes());
////				imageUUIDs.add(imageUUID);
////			} else {
////				imageUUIDs.add(imgName);
////			}
////		}
//		List<String> imgNames = new ArrayList<>(imgNamesSet);
//
//		Set<String> imageUUIDs = new HashSet<>();
//
//		for (int i = 0; i < files.length; i++) {
//			MultipartFile file = files[i];
//			String imgName = imgNames.get(i);  // Corrected: Accessing elements using get()
//
//			if (!file.isEmpty()) {
//				String imageUUID = UUID.randomUUID().toString(); // Generate a unique image name
//				Path filenamePath = Paths.get(uploadDir, imageUUID);
//				Files.write(filenamePath, file.getBytes());
//				imageUUIDs.add(imageUUID);
//			} else {
//				imageUUIDs.add(imgName);
//			}
//		}
//		if (productService.isProductExists(productDTO.getName()))
//		{
//			System.out.println("Product exists with the same name");
//			attributes.addFlashAttribute("error", "A product with this name already exists");
//			return "redirect:/admin/products/add";
//		}
////		if(result.hasFieldErrors("brand"))
//			if (product.getName()==null){
//			System.out.println("Some error occurred");
//			attributes.addFlashAttribute("error", "Do not leave blank space for name");
////			return "redirect:/admin/products/add";
//			return "productsAdd";
//		}
////		if(result.hasFieldErrors("name"))
//		if (product.getBrand()==null)
//		{
//			System.out.println("Some error occurred");
//			attributes.addFlashAttribute("error", "Do not leave empty space fo brand");
//			return "redirect:/admin/products/add";
////			return "productsAdd";
//		}
//
//		if (result.hasErrors()) {
//////			model.addAttribute("productDTO", productDTO);
////			attributes.addFlashAttribute("error", "error");
//////			return "redirect:/admin/products/add";
//			for (FieldError error :result.getFieldErrors()){
//				System.out.println("F  " +error.getField() + " Message "  + error.getDefaultMessage());
//			}
//			return "productsAdd";
//		}
//		product.setImageNames(imageUUIDs);
//		productService.addProduct(product);
//		return "redirect:/admin/products";
//	}
}
