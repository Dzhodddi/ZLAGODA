import { Route } from "react-router-dom";
import {EmployeeList} from "@/features/employee/components/employeeList.tsx";
import {UpsertEmployeeForm} from "@/features/employee/components/employeeForm.tsx";
import {ProtectedRoute} from "@/components/protectedRoutes.tsx";
import {EmployeeMe} from "@/features/employee/components/employeeMe.tsx";
import {EmployeeGet} from "@/features/employee/components/employeeGet.tsx";



const employeeRoutes = (
    <>
        <Route element={<ProtectedRoute allowedRoles={["MANAGER"]} />}>
            <Route path="/employee" element={<EmployeeList/>} />
            <Route path="/employee/create" element={<UpsertEmployeeForm/>} />
            <Route path="/employee/edit/:id" element={<UpsertEmployeeForm/>} />
            <Route path="/employee/:id" element={<EmployeeGet/>} />
        </Route>
        <Route element={<ProtectedRoute allowedRoles={["CASHIER"]} />}>
            <Route path="/employee/me" element={<EmployeeMe/>} />
        </Route>
    </>
);

export { employeeRoutes }