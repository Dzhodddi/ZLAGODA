import { Route } from "react-router-dom";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {EmployeeList} from "@/features/employee/routes/EmployeeList.tsx";
import {CreateEmployeePage} from "@/features/employee/routes/CreateEmployeePage.tsx";
import {EditEmployeePage} from "@/features/employee/routes/EditEmployeePage.tsx";
import {EmployeePage} from "@/features/employee/routes/EmployeePage.tsx";
import {OwnEmployeePage} from "@/features/employee/routes/EmployeeMePage.tsx";



const employeeRoutes = (
    <>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
            <Route path="/employee" element={<EmployeeList/>} />
            <Route path="/employee/create" element={<CreateEmployeePage/>} />
            <Route path="/employee/edit/:id" element={<EditEmployeePage/>} />
            <Route path="/employee/:id" element={<EmployeePage/>} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
            <Route path="/employee/me" element={<OwnEmployeePage/>} />
        </Route>
    </>
);

export { employeeRoutes }