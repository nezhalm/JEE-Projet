

package com.example.demo;
import Dao.DaoImplementation.ClientImp;
import Dao.DaoImplementation.EmployeImp;
import Entities.Client;
import Entities.Employe;
import Service.ClientService;
import Service.EmployeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static Dao.DaoImplementation.EmployeImp.genererCodeUnique;


@WebServlet("/dashboard")
public class AllClientsServlet extends HttpServlet {
    private final ClientService clientService;
    private final EmployeService employeService;

    public AllClientsServlet() {
        this.clientService = new ClientService(new ClientImp());
        this.employeService = new EmployeService(new EmployeImp());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action2 = request.getParameter("action");
        if ("updateClient".equals(action2)) {
            String clientCode = request.getParameter("clientCode");
            request.setAttribute("employes", employeService.AllEmployes());
            request.setAttribute("clientTrouvee", clientService.chercher(clientCode));
            request.getRequestDispatcher("/WEB-INF/JSPs/ClientAdministration/UpdateClient.jsp").forward(request, response);
        } else if ("searchClient".equals(action2)){
            String clientName = request.getParameter("name");
           Client client = clientService.chercher(clientName);
            if (client != null) {
                request.setAttribute("client", client);
                request.getRequestDispatcher("/WEB-INF/JSPs/ClientAdministration/GetAllClients.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?successMessage=Le+client+introuvable.");
            }

        } else {
            request.setAttribute("clients", clientService.AllClients());
            request.getRequestDispatcher("/WEB-INF/JSPs/ClientAdministration/Dashboard.jsp").forward(request, response);
        }
    }



    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("deleteClient".equals(action)) {
            String clientCode = request.getParameter("clientCode");
            boolean deleted = clientService.chercherPourSupprimer(clientCode);

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/dashboard?successMessage=Le+client+a+ete+supprime+avec+succes.");
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard?errorMessage=La+suppression+du+client+a+echoue.");
            }
        } else if ("addClient".equals(action)) {
            // Récupération des données du formulaire
            String fullName = request.getParameter("fullName");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String dateNaissanceStr = request.getParameter("dateNaissance");
            // Validation des données (vous devrez ajouter une validation appropriée)
            if (fullName == null || username == null || email == null || phoneNumber == null || dateNaissanceStr == null) {
                request.setAttribute("errorMessage", "Données du formulaire incorrectes.");
                request.getRequestDispatcher("/WEB-INF/JSPs/erreur.jsp").forward(request, response);}

            LocalDate dateNaissance = LocalDate.parse(dateNaissanceStr);
            String matricule = request.getParameter("matricule");
            // Création d'un nouvel objet Client
            Client newClient = new Client();
            newClient.setCode(genererCodeUnique(3));
            newClient.setNom(fullName);
            newClient.setPrenom(username);
            newClient.setDateNaissance(dateNaissance);
            newClient.setAdresse(email);
            newClient.setTelephone(phoneNumber);
            // Recherche de l'employé associé par matricule
            Employe employe = employeService.chercher(matricule);

            if (employe != null) {
                newClient.setCreator(employe);

                Optional<Client> inserted = clientService.insertClient(newClient);

                if (inserted.isPresent()) {
                    response.sendRedirect(request.getContextPath() + "/dashboard?successMessage=Le+client+a+ete+ajoute+avec+succes.");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard?errorMessage=error+dans+l'ajout+du+client.");

                }
            } else {
                request.setAttribute("errorMessage", "Employé non trouvé.");
                request.getRequestDispatcher("/WEB-INF/JSPs/erreur.jsp").forward(request, response);
            }
        }else if ("savechangesClient".equals(action)) {
            String fullName2 = request.getParameter("fullName");
            String code = request.getParameter("code");
            String username2 = request.getParameter("username");
            String adresse = request.getParameter("email");
            String phoneNumber2 = request.getParameter("phoneNumber");
            LocalDate dateNaissance = LocalDate.parse(request.getParameter("dateNaissance"));
            Client client = new Client(fullName2,username2, phoneNumber2 ,dateNaissance,code,adresse);
            boolean var =clientService.update(client);
            if(var){
                response.sendRedirect(request.getContextPath() + "/dashboard?successMessage=Le+client+a+ete+mise+a+jour+avec+succes.");
            }else{
                response.sendRedirect(request.getContextPath() + "/dashboard?errorMessage=error+dans+la+modification+du+client.");
            }
        }
    }


}

