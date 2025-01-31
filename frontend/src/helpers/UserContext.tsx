import { createContext, useState, useEffect, ReactNode } from 'react';
import axios from 'axios';


const apiUrl = new URL('/api/user', import.meta.env.VITE_API_URL).toString();

// structure of the user data 
interface User {
    id: string;
    username: string;
}

// structure of the context value
interface UserContextType {
    user: User | null;
    setUser: React.Dispatch<React.SetStateAction<User | null>>;
}

export const UserContext = createContext<UserContextType | undefined>(undefined);

// type for the UserProvider component's props
interface UserProviderProps {
    children: ReactNode;
}

export const UserProvider = ({ children }: UserProviderProps) => {
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const fetchUser = async () => {
            const accessToken = sessionStorage.getItem("accessToken");
            if (accessToken) {
                try {
                    const response = await axios.get<User>(`${apiUrl}`, {
                        headers: { Authorization: `Bearer ${accessToken}` },
                    });
                    setUser(response.data);
                } catch (error) {
                    console.error("Error fetching user data:", error);
                }
            }
        };

        fetchUser();
    }, []);

    return (
        <UserContext.Provider value={{ user, setUser }}>
            {children}
        </UserContext.Provider>
    );
};
